package cn.gybyt.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    static {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        Long timeOut = System.getProperty("gybyt.okhttp.timeOut") == null ? 5000 : Long.parseLong(System.getProperty("gybyt.okhttp.timeOut"));
        int maxCon = System.getProperty("gybyt.okhttp.maxCon") == null ? 10 : Integer.parseInt(System.getProperty("gybyt.okhttp.maxCon"));
        ConnectionPool connectionPool = new ConnectionPool(maxCon, timeOut, TimeUnit.MILLISECONDS);
        clientBuilder.callTimeout(timeOut, TimeUnit.MILLISECONDS);
        clientBuilder.connectionPool(connectionPool);
        client = clientBuilder.build();
    }

    /**
     * 发送请求
     * @param url
     * @param paramMap
     * @param headerMap
     * @param data
     * @param typeUtil
     * @return
     * @param <T>
     */
    public static <T> T fetch(String url, Map<String, Object> paramMap, Map<String, String> headerMap, Object data, Method method, Media media, TypeUtil<T> typeUtil) {
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody body = null;
        if (media == Media.FORM) {
            JSONObject jsonObject = JSONObject.from(data, JSONWriter.Feature.FieldBased);
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            jsonObject.forEach((k, v) -> formBodyBuilder.add(k, String.valueOf(v)));
            if (BaseUtil.isNotEmpty(paramMap)) {
                paramMap.forEach((k, v) -> formBodyBuilder.add(k, String.valueOf(v)));
            }
            body = formBodyBuilder.build();
        }
        if (media == null || media == Media.JSON) {
            body = RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(data, JSONWriter.Feature.FieldBased));
        }
        if (BaseUtil.isNotEmpty(paramMap) && media != Media.FORM) {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(url);
            urlBuilder.append("?");
            paramMap.forEach((k, v) -> {
                urlBuilder.append(k);
                urlBuilder.append("=");
                urlBuilder.append(v);
                urlBuilder.append("&");
            });
            url = urlBuilder.toString().replaceAll("&$", "");
        }
        requestBuilder.url(url)
                .method(method.name(), body);
        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            byte[] dataBytes = response.body().bytes();
            try {
                log.debug("请求 {} 成功", url);
                log.debug(new String(dataBytes));
                return JSON.parseObject(dataBytes, typeUtil.getType(), JSONReader.Feature.FieldBased);
            } catch (Exception e) {
                return (T) new String(dataBytes);
            }
        } catch (IOException e) {
            log.error("请求 {} 失败", url);
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
        FORM
    }

}
