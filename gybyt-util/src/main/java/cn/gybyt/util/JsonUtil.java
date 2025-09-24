package cn.gybyt.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.lang.reflect.Type;

/**
 * json工具类
 *
 * @program: gybyt-tools
 * @classname: JsonUtil
 * @author: codetiger
 * @create: 2025/6/13 15:22
 **/
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 允许检测所有可见性级别的字段
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 忽略所有getter
        MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        // 忽略所有setter
        MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        // 忽略所有isGetter
        MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param o 对象
     * @return JSON字符串
     */
    @SneakyThrows
    public static String toJson(Object o) {
        if (BaseUtil.isEmpty(o)) {
            return "";
        }
        if (o instanceof String) {
            return (String) o;
        }
        return MAPPER.writeValueAsString(o);
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param data JSON字符串
     * @param type      目标类型
     * @param <T>        泛型类型
     * @return 转换后的对象
     */
    @SneakyThrows
    public static <T> T parseObject(String data, TypeUtil<T> type) {
        return MAPPER.readValue(data, new TypeReference<T>() {
            @Override
            public Type getType() {
                return type.getType();
            }
        });
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param data JSON字符串字节数组
     * @param type      目标类型
     * @param <T>        泛型类型
     * @return 转换后的对象
     */
    @SneakyThrows
    public static <T> T parseObject(byte[] data, TypeUtil<T> type) {
        return MAPPER.readValue(data, new TypeReference<T>() {
            @Override
            public Type getType() {
                return type.getType();
            }
        });
    }

}
