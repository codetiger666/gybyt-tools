package cn.gybyt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * spring 工具类
 *
 * @program: utils
 * @classname: SpringUtil
 * @author: codetiger
 * @create: 2022/11/9 19:47
 **/
@SuppressWarnings("unchecked")
@Component("gybytSpringUtil")
public class SpringUtil implements BeanFactoryPostProcessor, ApplicationContextAware {

    private final static Logger log = LoggerFactory.getLogger(SpringUtil.class);
    private static ApplicationContext context;
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringUtil.context = context;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtil.beanFactory = beanFactory;
    }

    private static ListableBeanFactory getBeanFactory() {
        if (BaseUtil.isNull(SpringUtil.context)) {
            return SpringUtil.beanFactory;
        }
        return SpringUtil.context;
    }

    /**
     * 根据类获取bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return getBeanFactory().getBean(clazz);
    }

    /**
     * 根据beanId获取对象
     *
     * @param beanName
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName) {
        if (BaseUtil.isEmpty(beanName)) {
            return null;
        }
        return (T) getBeanFactory().getBean(beanName);
    }

    /**
     * 根据beanId、类型获取对象
     *
     * @param beanName
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz, String beanName) {
        if (BaseUtil.isEmpty(beanName) || "".equals(beanName.trim())) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        return (T) getBeanFactory().getBean(beanName, clazz);
    }

    /**
     * 根据beanId、类型获取对象
     *
     * @param beanName
     * @return
     */
    public static void registerBean(Object o, String beanName) {
        beanFactory.registerSingleton(beanName, o);
    }

    /**
     * 根据类型获取对象集合
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return getBeanFactory().getBeansOfType(clazz);
    }

    /**
     * 获取ApplicationContext对象
     *
     * @return
     */
    public static ApplicationContext getContext() {
        if (context == null) {
            return null;
        }
        return context;
    }

    /**
     * 发布事件
     *
     * @param event
     */
    public static void publishEvent(ApplicationEvent event) {
        if (context == null) {
            return;
        }
        try {
            context.publishEvent(event);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * 获取请求对象
     *
     * @return
     */
    public static HttpServletRequest getServletRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (BaseUtil.isNull(requestAttributes)) {
            return null;
        }
        return requestAttributes.getRequest();
    }

    /**
     * 获取响应对象
     *
     * @return
     */
    public static HttpServletResponse getServletResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (BaseUtil.isNull(requestAttributes)) {
            return null;
        }
        return requestAttributes.getResponse();
    }

    /**
     * 获取请求参数map
     *
     * @return
     */
    public static Map<String, String> getRequestParam() {
        HashMap<String, String> paramMap = new HashMap<>();
        HttpServletRequest request = getServletRequest();
        if (BaseUtil.isNull(request)) {
            return new HashMap<>(0);
        }
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            paramMap.put(key, request.getParameter(key));
        }
        return paramMap;
    }

    /**
     * 获取请求体
     *
     * @return
     */
    public static String getRequestBody() {
        // 获取请求对象
        HttpServletRequest request = getServletRequest();
        if (BaseUtil.isNull(request)) {
            return "";
        }
        // 新建存储对象
        StringBuilder builder = new StringBuilder();
        if (request.getContentLength() != -1) {
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
            }
        }
        return builder.toString();
    }

    /**
     * 获取请求体
     *
     * @return
     */
    public static byte[] getRequestBodyByte() {
        // 获取请求对象
        HttpServletRequest request = getServletRequest();
        if (BaseUtil.isNull(request)) {
            return new byte[0];
        }
        byte[] body;
        // 请求体为空时不再执行读操作
        if (request.getContentLength() != -1) {
            try {
                // 获取输出流
                ServletInputStream inputStream = request.getInputStream();
                body = FileUtil.readInputStream(inputStream);
            } catch (IOException e) {
                body = new byte[0];
            }
        } else {
            body = new byte[0];
        }
        return body;
    }

    /**
     * 获取请求头
     *
     * @return
     */
    public static Map<String, String> getRequestHeaders() {
        HashMap<String, String> headersMap = new HashMap<>();
        HttpServletRequest request = getServletRequest();
        if (BaseUtil.isNull(request)) {
            return new HashMap<>(0);
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            headersMap.put(key, request.getHeader(key));
        }
        return headersMap;
    }

    /**
     * 获取请求头
     *
     * @return
     */
    public static String getRequestHeader(String name) {
        if (BaseUtil.isEmpty(name)) {
            return "";
        }
        HttpServletRequest request = getServletRequest();
        return request.getHeader(name);
    }

    /**
     * 获取响应头
     *
     * @return
     */
    public static Map<String, String> getResponseHeaders() {
        HashMap<String, String> headersMap = new HashMap<>();
        HttpServletResponse response = getServletResponse();
        if (BaseUtil.isNull(response)) {
            return new HashMap(0);
        }
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.forEach(headerName -> {
            headersMap.put(headerName, response.getHeader(headerName));
        });
        return headersMap;
    }

    /**
     * 添加环境变量
     *
     * @param key
     * @param value
     * @param isReplace
     */
    public static synchronized void addProperty(String key, String value, Boolean isReplace) {
        Properties properties = System.getProperties();
        if (BaseUtil.isNotNull(properties.getProperty(key)) && !isReplace) {
            return;
        }
        properties.setProperty(key, value);
    }

    /**
     * 添加环境变量
     *
     * @param key
     * @param value
     * @param isReplace
     */
    public static synchronized void addProperty(Object key, Object value, Boolean isReplace) {
        Properties properties = System.getProperties();
        if (BaseUtil.isNotNull(properties.get(key)) && !isReplace) {
            return;
        }
        properties.put(key, value);
    }

    /**
     * 添加环境变量
     *
     * @param map
     * @param isReplace
     */
    public static synchronized void addProperty(Map<Object, Object> map, Boolean isReplace) {
        Properties properties = System.getProperties();
        map.keySet().forEach(key -> {
            if (BaseUtil.isNotNull(properties.get(key)) && !isReplace) {
                return;
            }
            properties.put(key, map.get(key));
        });
    }

    /**
     * 获取环境变量
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return getContext().getEnvironment().getProperty(key);
    }

}
