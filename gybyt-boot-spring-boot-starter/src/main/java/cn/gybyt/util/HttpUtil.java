package cn.gybyt.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * http工具类(使用HttpTemplate)
 *
 * @program: utils
 * @classname: HttpUtil
 * @author: codetiger
 * @create: 2023/3/11 12:50
 **/
public class HttpUtil {

    // restTemplate对象
    private static final RestTemplate restTemplate;

    static {
        restTemplate = SpringUtil.getBean(RestTemplate.class);
    }

    /**
     * 发送请求
     * @param url 请求链接
     * @param method 请求方式
     * @param body 请求体
     * @param headerMap 请求头
     * @param uriVariables 请求参数
     * @param type 返回值类型(为空默认返回字符串)
     * @return
     */
    public static <T> ResponseEntity<T> fetch(String url, HttpMethod method, Object body, MultiValueMap<String, String> headerMap, Map<String, Object> uriVariables, ParameterizedTypeReference<T> type) {
        RequestEntity requestEntity = null;
        // 处理空对象
        if (BaseUtil.isEmpty(headerMap)) {
            headerMap = new HttpHeaders();
        }
        if (BaseUtil.isEmpty(type)) {
            type = (ParameterizedTypeReference<T>) new ParameterizedTypeReference<String>() {};
        }
        if (BaseUtil.isNotEmpty(uriVariables)) {
            Set<String> keys = uriVariables.keySet();
            StringBuilder urlBuild = new StringBuilder();
            url += "?";
            keys.forEach(key -> {
                if (urlBuild.length() > 0) {
                    urlBuild.append("&");
                    urlBuild.append(key);
                    urlBuild.append("=");
                    urlBuild.append(uriVariables.get(key));
                } else {
                    urlBuild.append(key);
                    urlBuild.append("=");
                    urlBuild.append(uriVariables.get(key));
                }
            });
            url += urlBuild.toString();
        }
        try {
            requestEntity = new RequestEntity<>(body, headerMap, HttpMethod.GET, new URI(url));
        } catch (URISyntaxException e) {
        }
        try {
            return restTemplate.exchange(new URI(url) , method, requestEntity, type);
        } catch (URISyntaxException e) {
            throw new BaseException("请求失败");
        }
    }
}
