package cn.gybyt.util;

import com.alibaba.fastjson2.JSON;

/**
 * fastjson工具类
 *
 * @program: utils
 * @classname: FastJsonUtil
 * @author: codetiger
 * @create: 2022/11/12 20:28
 **/
public class FastJsonUtil {

    /**
     * 序列化
     *
     * @param o
     * @return
     */
    public static String toJson(Object o) {
        return JSON.toJSONString(o);
    }

    /**
     * 反序列化
     *
     * @param json
     * @param type
     * @return
     */
    public static <T> T toObject(String json, Class<T> type) {
        return JSON.parseObject(json, type);
    }

    /**
     * 反序列化
     *
     * @param json
     * @param className
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String json, String className) {
        try {
            Class<T> aClass = (Class<T>) Class.forName(className);
            return JSON.parseObject(json, aClass);
        } catch (ClassNotFoundException e) {
            throw new BaseException(e.getMessage());
        }
    }

}
