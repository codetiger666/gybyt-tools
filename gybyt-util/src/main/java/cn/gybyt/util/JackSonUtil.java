package cn.gybyt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JackSonUtil工具类
 *
 * @program: utils
 * @classname: JackSonUtil
 * @author: codetiger
 * @create: 2022/11/12 20:21
 **/
public class JackSonUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    /**
     * 序列化
     * @param o
     * @return
     */
    public static String toJson(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 反序列化
     * @param json
     * @param type
     * @return
     */
    public static <T> T toObject(String json, Class<T> type){
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 反序列化
     * @param json
     * @param className
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String json, String className){
        try {
            Class<T> aClass = (Class<T>) Class.forName(className);
            return objectMapper.readValue(json, aClass);
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new BaseException(e.getMessage());
        }
    }

}
