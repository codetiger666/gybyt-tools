package cn.gybyt.util;

import cn.gybyt.concurrent.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * okhttp工具类
 *
 * @program: gybyt-tools
 * @classname: OkHttpUtil
 * @author: codetiger
 * @create: 2023/11/9 19:21
 **/
@Slf4j
public class HttpUtil {

    /**
     * client实例
     */
    private final static OkHttpClient client;
    /**
     * URL参数正则表达式
     */
    private final static Pattern URL_PARAMPATTERN = Pattern.compile("^.*?\\?(?:[\\w\\-\\\\.%]*=[\\w\\-\\\\.%]*)+$");
    /**
     * 异步请求线程池
     */
    private final static ThreadPoolExecutor SYNC_THREAD_POOL = new ThreadPoolExecutor(
            10,
            20,
            60L,
            TimeUnit.SECONDS,
            new BlockingArrayQueue<>(30),
            new NamedThreadFactory("okhttp-sync-thread-pool"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    static {
        Long timeOut = System.getProperty("gybyt.okhttp.timeOut") == null ? 5000 : Long.parseLong(
                System.getProperty("gybyt.okhttp.timeOut"));
        int maxCon = System.getProperty("gybyt.okhttp.maxCon") == null ? 30 : Integer.parseInt(
                System.getProperty("gybyt.okhttp.maxCon"));
        Dispatcher dispatcher = new Dispatcher(SYNC_THREAD_POOL);
        dispatcher.setMaxRequests(maxCon);
        dispatcher.setMaxRequestsPerHost(maxCon);
        client = new OkHttpClient.Builder().callTimeout(timeOut, TimeUnit.MILLISECONDS)
                                           .connectionPool(new ConnectionPool(maxCon, timeOut, TimeUnit.MILLISECONDS))
                                           .dispatcher(dispatcher)
                                           .build();
    }

    /**
     * 发送请求
     *
     * @param url       请求地址
     * @param paramMap  请求参数
     * @param headerMap 请求头
     * @param data      请求体
     * @param method    请求方法
     * @param media     请求媒体类型
     * @param typeUtil  返回类型
     * @param <T>       返回类型泛型
     * @return 返回结果
     */
    public static <T> T fetch(String url, Map<String, Object> paramMap, Map<String, String> headerMap, Object data, Method method, Media media, TypeUtil<T> typeUtil) {
        try {
            return HttpUtil.handleResponse(url, paramMap, data, media,
                                           client.newCall(handleRequest(url, paramMap, headerMap, data, method, media))
                                                 .execute()
                                                 .body(), typeUtil, null);
        } catch (Exception e) {
            return HttpUtil.handleError(url, paramMap, data, media, e, null);
        }
    }

    /**
     * 异步请求
     *
     * @param url           请求地址
     * @param paramMap      请求参数
     * @param headerMap     请求头
     * @param data          请求体
     * @param method        请求方法
     * @param media         请求媒体类型
     * @param typeUtil      返回类型
     * @param successHandle 成功回调
     * @param errorHandle   失败回调
     * @param <T>           返回类型泛型
     */
    public static <T> void fetchSync(String url, Map<String, Object> paramMap, Map<String, String> headerMap, Object data, Method method, Media media, TypeUtil<T> typeUtil, Consumer<T> successHandle, Consumer<Exception> errorHandle) {
        try {
            Request request = handleRequest(url, paramMap, headerMap, data, method, media);
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    HttpUtil.handleError(url, paramMap, data, media, e, null);
                    if (BaseUtil.isNotEmpty(errorHandle)) {
                        errorHandle.accept(e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (BaseUtil.isNotEmpty(successHandle)) {
                        successHandle.accept(
                                HttpUtil.handleResponse(url, paramMap, data, media, response.body(), typeUtil,
                                                        errorHandle));
                    }
                }
            });
        } catch (Exception e) {
            HttpUtil.handleError(url, paramMap, data, media, e, errorHandle);
        }
    }

    /**
     * 处理响应
     *
     * @param url          请求地址
     * @param paramMap     请求参数
     * @param data         请求体
     * @param media        请求媒体类型
     * @param responseBody 响应体
     * @param typeUtil     返回类型
     * @param errorHandle  失败回调
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T handleResponse(String url, Map<String, Object> paramMap, Object data, Media media, ResponseBody responseBody, TypeUtil<T> typeUtil, Consumer<Exception> errorHandle) {
        try {
            if (responseBody == null) {
                return null;
            }
            if (typeUtil.getType()
                        .getTypeName()
                        .equals("java.io.InputStream")) {
                return (T) responseBody.byteStream();
            }
            byte[] dataBytes = responseBody
                    .bytes();
            try {
                log.debug("请求 {} 成功", url);
                log.debug(new String(dataBytes));
                return JsonUtil.parseObject(dataBytes, typeUtil);
            } catch (Exception e) {
                return (T) new String(dataBytes);
            }
        } catch (IOException e) {
            return HttpUtil.handleError(url, paramMap, data, media, e, errorHandle);
        }
    }

    /**
     * 处理异常
     *
     * @param url         请求地址
     * @param paramMap    请求参数
     * @param data        请求体
     * @param media       请求媒体类型
     * @param e           异常
     * @param errorHandle 失败回调
     * @param <T>         响应泛型
     */
    private static <T> T handleError(String url, Map<String, Object> paramMap, Object data, Media media, Exception e, Consumer<Exception> errorHandle) {
        if (media == Media.JSON) {
            log.error("请求 {} 失败, 请求体{}", url, JsonUtil.toJson(data));
        } else {
            Map<String, Object> dataMap = new HashMap<>();
            if (BaseUtil.isNotEmpty(paramMap)) {
                dataMap.putAll(paramMap);
            }
            if (BaseUtil.isNotEmpty(data)) {
                dataMap.putAll(JsonUtil.parseObject(JsonUtil.toJson(data), new TypeUtil<Map<? extends String, ?>>() {}));
            }
            log.error("请求 {} 失败, 请求体{}", url, JsonUtil.toJson(dataMap));
        }
        log.error("请求失败", e);
        if (BaseUtil.isNotEmpty(errorHandle)) {
            errorHandle.accept(e);
        }
        return null;
    }

    /**
     * 处理请求
     *
     * @param url       请求地址
     * @param paramMap  请求参数
     * @param headerMap 请求头
     * @param data      请求体
     * @param method    请求方法
     * @param media     请求媒体类型
     * @return
     */
    private static Request handleRequest(String url, Map<String, Object> paramMap, Map<String, String> headerMap, Object data, Method method, Media media) {
        // 默认GET请求
        if (method == null) {
            method = Method.GET;
        }
        // 默认JSON请求
        if (media == null) {
            media = Media.JSON;
        }
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody body = null;
        if (media == Media.JSON) {
            if (BaseUtil.isNotEmpty(paramMap)) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(url);
                if (HttpUtil.URL_PARAMPATTERN.matcher(url)
                                             .find()) {
                    urlBuilder.append("&");
                } else {
                    urlBuilder.append("?");
                }
                paramMap.forEach((k, v) -> {
                    urlBuilder.append(k);
                    urlBuilder.append("=");
                    urlBuilder.append(v);
                    urlBuilder.append("&");
                });
                url = urlBuilder.toString()
                                .replaceAll("&$", "");
            }
            body = RequestBody.create(MediaType.parse("application/json"),
                                      JsonUtil.toJson(data));
        }
        if (media == Media.X_FROM) {
            Map<String, Object> jsonMap = JsonUtil.parseObject(JsonUtil.toJson(data),
                                                                       new TypeUtil<Map<String, Object>>() {
                                                                       });
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            jsonMap.forEach((k, v) -> formBodyBuilder.add(k, String.valueOf(v)));
            if (BaseUtil.isNotEmpty(paramMap)) {
                paramMap.forEach((k, v) -> formBodyBuilder.add(k, String.valueOf(v)));
            }
            body = formBodyBuilder.build();
        }
        if (media == Media.FORM) {
            Map<String, Object> jsonMap = JsonUtil.parseObject(JsonUtil.toJson(data),
                                                               new TypeUtil<Map<String, Object>>() {
                                                               });
            MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder();
            jsonMap.forEach((k, v) -> formBodyBuilder.addFormDataPart(k, String.valueOf(v)));
            if (BaseUtil.isNotEmpty(paramMap)) {
                paramMap.forEach((k, v) -> {
                    if (v instanceof File) {
                        formBodyBuilder.addFormDataPart(k, ((File) v).getName(),
                                                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                                                           (File) v));
                        return;
                    }
                    formBodyBuilder.addFormDataPart(k, String.valueOf(v));
                });
            }
            try {
                body = formBodyBuilder.build();
            } catch (IllegalStateException e) {
                log.debug("请求体构建失败", e);
                body = formBodyBuilder.addFormDataPart("", "")
                                      .build();
            }
        }
        requestBuilder.url(url)
                      .method(method.name(), body);
        if (BaseUtil.isNotEmpty(headerMap)) {
            headerMap.forEach(requestBuilder::addHeader);
        }
        return requestBuilder.build();
    }

    public enum Method {
        GET,
        HEAD,
        POST,
        PUT,
        PATCH,
        DELETE,
        OPTIONS,
        TRACE
    }

    public enum Media {
        JSON,
        FORM,
        X_FROM,
        NONE
    }

    /**
     * 阻塞队列,一直尝试插入任务，阻塞等待，不抛出异常
     */
    static class BlockingArrayQueue<E> extends ArrayBlockingQueue<E> {
        public BlockingArrayQueue(int capacity) {
            super(capacity);
        }

        @Override
        public boolean offer(E e) {
            try {
                // 阻塞直到队列有空位
                put(e);
                return true;
            } catch (InterruptedException ex) {
                Thread.currentThread()
                      .interrupt();
                return false;
            }
        }
    }

}
