package cn.gybyt.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
public class OkHttpUtil {

    private final static OkHttpClient client;
    private final static Pattern URL_PARAMPATTERN = Pattern.compile("^.*?\\?(?:[\\w\\-\\\\.%]*=[\\w\\-\\\\.%]*)+$");

    static {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        Long timeOut = System.getProperty("gybyt.okhttp.timeOut") == null ? 5000 : Long.parseLong(
                System.getProperty("gybyt.okhttp.timeOut"));
        int maxCon = System.getProperty("gybyt.okhttp.maxCon") == null ? 10 : Integer.parseInt(
                System.getProperty("gybyt.okhttp.maxCon"));
        ConnectionPool connectionPool = new ConnectionPool(maxCon, timeOut, TimeUnit.MILLISECONDS);
        clientBuilder.callTimeout(timeOut, TimeUnit.MILLISECONDS);
        clientBuilder.connectionPool(connectionPool);
        client = clientBuilder.build();
    }

    /**
     * 发送请求
     *
     * @param url       请求地址
     * @param paramMap  请求参数
     * @param headerMap 请求头
     * @param data      请求体
     * @param typeUtil  返回类型
     * @param <T>       返回类型泛型
     * @return 返回结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T fetch(String url, Map<String, Object> paramMap, Map<String, String> headerMap, Object data, Method method, Media media, TypeUtil<T> typeUtil) {
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
                if (OkHttpUtil.URL_PARAMPATTERN.matcher(url)
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
                                      JSON.toJSONString(data, JSONWriter.Feature.FieldBased));
        }
        if (media == Media.X_FROM) {
            JSONObject jsonObject = JSONObject.from(data, JSONWriter.Feature.FieldBased);
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            jsonObject.forEach((k, v) -> formBodyBuilder.add(k, String.valueOf(v)));
            if (BaseUtil.isNotEmpty(paramMap)) {
                paramMap.forEach((k, v) -> formBodyBuilder.add(k, String.valueOf(v)));
            }
            body = formBodyBuilder.build();
        }
        if (media == Media.FORM) {
            JSONObject jsonObject = JSONObject.from(data, JSONWriter.Feature.FieldBased);
            MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder();
            jsonObject.forEach((k, v) -> formBodyBuilder.addFormDataPart(k, String.valueOf(v)));
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
        try {
            ResponseBody responseBody = client.newCall(requestBuilder.build())
                                              .execute()
                                              .body();
            if (responseBody == null) {
                return null;
            }
            if (typeUtil.getType().getTypeName().equals("java.io.InputStream")) {
                return (T) responseBody.byteStream();
            }
            byte[] dataBytes = responseBody
                    .bytes();
            try {
                log.debug("请求 {} 成功", url);
                log.debug(new String(dataBytes));
                return JSON.parseObject(dataBytes, typeUtil.getType(), JSONReader.Feature.FieldBased);
            } catch (Exception e) {
                return (T) new String(dataBytes);
            }
        } catch (IOException e) {
            if (media == Media.JSON) {
                log.error("请求 {} 失败, 请求体{}", url, JSON.toJSONString(data, JSONWriter.Feature.FieldBased));
            } else {
                Map<String, Object> dataMap = new HashMap<>(paramMap);
                dataMap.putAll(JSON.parseObject(JSON.toJSONString(data, JSONWriter.Feature.FieldBased)));
                log.error("请求 {} 失败, 请求体{}", url, JSON.toJSONString(dataMap, JSONWriter.Feature.FieldBased));
            }
            log.error("请求失败", e);
            return null;
        }

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

}
