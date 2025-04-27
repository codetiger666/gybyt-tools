package cn.gybyt.interceptor;

import cn.gybyt.util.SpringUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Map;

/**
 * feign请求头配置
 * @program: utils
 * @classname: GybytFeignRequestHeaderInterceptor
 * @author: codetiger
 * @create: 2023/3/5 17:56
 **/
public class GybytFeignRequestHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, String> headers = SpringUtil.getRequestHeaders();
        if (!headers.isEmpty()) {
            headers.forEach(requestTemplate::header);
        }
    }
}
