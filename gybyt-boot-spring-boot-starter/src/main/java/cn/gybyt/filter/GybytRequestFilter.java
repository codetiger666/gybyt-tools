package cn.gybyt.filter;

import cn.gybyt.wrapper.GybytHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Request全局过滤处理
 *
 * @program: utils
 * @classname: GybytRequestFilter
 * @author: codetiger
 * @create: 2023/1/17 19:31
 **/
public class GybytRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 替换默认请求包装器
        GybytHttpServletRequestWrapper gybytHttpServletRequestWrapper = new GybytHttpServletRequestWrapper((HttpServletRequest) request);
        // 执行过滤
        chain.doFilter(gybytHttpServletRequestWrapper, response);
    }
}
