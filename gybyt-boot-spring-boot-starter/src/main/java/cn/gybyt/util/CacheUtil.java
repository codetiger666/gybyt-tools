package cn.gybyt.util;

import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 *
 * @program: utils
 * @classname: CacheUtil
 * @author: codetiger
 * @create: 2022/11/9 19:43
 **/
public class CacheUtil {

    private static RedisTemplate redisTemplate;
    private static RedisTemplate stringRedisTemplate;

    static {
        redisTemplate = SpringUtil.getBean(RedisTemplate.class, "gybytRedisTemplate");
        stringRedisTemplate = SpringUtil.getBean(RedisTemplate.class, "gybytRedisStringTemplate");
    }

    /**
     * 获取缓存
     * @param cacheName 缓存名称
     * @param preKey 缓存key前缀
     * @param key 缓存key
     * @param callable 回调方法
     * @param timeout 超时时间(毫秒)
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T get(String cacheName, String preKey, String key, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            set(genKey(cacheName, preKey, key), t, timeout);
        }
        return t;
    }

    /**
     * 获取缓存
     * @param cacheName 缓存名称
     * @param preKey 缓存key前缀
     * @param key 缓存key
     * @param callable 回调方法
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T get(String cacheName, String preKey, String key, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            set(genKey(cacheName, preKey, key), t, null);
        }
        return t;
    }

    /**
     * 获取缓存
     * @param cacheName 缓存名称
     * @param preKey 缓存key前缀
     * @param key 缓存key
     * @return 返回值
     * @param <T> 泛型
     */
    public static <T> T get(String cacheName, String preKey, String key) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        }
        return t;
    }

    /**
     * 获取缓存
     * @param key 缓存key
     * @param callable 回调方法
     * @param timeout 超时时间(毫秒)
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T get(String key, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(key);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(key);
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            set(key, t, timeout);
        }
        return t;
    }


    /**
     * 获取缓存
     * @param key 缓存key
     * @param callable 回调方法
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T get(String key, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(key);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(key);
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            set(key, t, null);
        }
        return t;
    }

    /**
     * 获取缓存
     * @param key 缓存key
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T get(String key) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(key);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(key);
        }
        return t;
    }

    /**
     * 获取Hash缓存
     * @param key 缓存key
     * @param hashKey hash key
     * @return 返回值
     * @param <T> 泛型
     */
    public static <T> T getHash(String key, Object hashKey) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(key, hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(key, hashKey);
        }
        return t;
    }

    /**
     * 获取Hash缓存
     * @param key 缓存key
     * @param hashKey hash key
     * @param callable 回调函数
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T getHash(String key, Object hashKey, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(key, hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(key, hashKey);
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            setHash(key, hashKey, t, null);
        }
        return t;
    }

    /**
     * 获取Hash缓存
     * @param key 缓存key
     * @param hashKey hash key
     * @param callable 回调函数
     * @param timeout 超时时间(毫秒)
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T getHash(String key, Object hashKey, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(key, hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(key, hashKey);
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            setHash(key, hashKey, t, timeout);
        }
        return t;
    }

    /**
     * 获取Hash缓存
     * @param key 缓存key
     * @param hashKey hash key
     * @return 返回值
     * @param <T> 泛型
     */
    public static <T> T getHash(String cacheName, String preKey, String key, Object hashKey) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        }
        return t;
    }
    /**
     * 获取Hash缓存
     * @param key 缓存key
     * @param hashKey hash key
     * @param callable 回调函数
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T getHash(String cacheName, String preKey, String key, Object hashKey, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            setHash(key, hashKey, t, null);
        }
        return t;
    }
    /**
     * 获取Hash缓存
     * @param key 缓存key
     * @param hashKey hash key
     * @param callable 回调函数
     * @param timeout 超时时间(毫秒)
     * @return 返回值
     * @param <T> 泛型
     */
    @SneakyThrows
    public static <T> T getHash(String cacheName, String preKey, String key, Object hashKey, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        }
        if (BaseUtil.isNull(t)) {
            t = callable.call();
            setHash(key, hashKey, t, timeout);
        }
        return t;
    }

    /**
     * 添加缓存(非null值)
     * @param key 缓存key
     * @param o 需缓存的对象
     * @param timeout 超时时间(毫秒)
     */
    public static void set(String key, Object o, Long timeout) {
        if (BaseUtil.isNull(o)) {
            return;
        }
        if (BaseUtil.isNull(timeout)) {
            timeout = 24 * 60 * 60 * 1000L;
        }
        if (timeout == -1L) {
            if (o instanceof String) {
                stringRedisTemplate.opsForValue().set(key, o);
                return;
            }
            redisTemplate.opsForValue().set(key, o);
        }
        if (o instanceof String) {
            stringRedisTemplate.opsForValue().set(key, o, genRandomTimeout(timeout), TimeUnit.MILLISECONDS);
            return;
        }
        redisTemplate.opsForValue().set(key, o, genRandomTimeout(timeout), TimeUnit.MILLISECONDS);
    }

    /**
     * 添加Hash缓存(非null值)
     * @param key 缓存key
     * @param o 需缓存的对象
     * @param timeout 超时时间(毫秒)
     */
    public static void setHash(String key, Object hashKey, Object o, Long timeout) {
        if (BaseUtil.isNull(o)) {
            return;
        }
        if (BaseUtil.isNull(timeout)) {
            timeout = 24 * 60 * 60 * 1000L;
        }
        if (timeout == -1L) {
            if (o instanceof String) {
                stringRedisTemplate.opsForHash().put(key, hashKey, o);
                return;
            }
            redisTemplate.opsForHash().put(key, hashKey, o);
        }
        if (o instanceof String) {
            stringRedisTemplate.opsForHash().put(key, hashKey, o);
            stringRedisTemplate.expire(key, genRandomTimeout(timeout), TimeUnit.MILLISECONDS);
            return;
        }
        redisTemplate.opsForHash().put(key, hashKey, o);
        redisTemplate.expire(key, genRandomTimeout(timeout), TimeUnit.MILLISECONDS);
    }

    /**
     * 不需要:结尾
     * @param cacheName
     * @param preKey
     * @param key
     * @return
     */
    private static String genKey(String cacheName, String preKey, String key) {
        return String.format("%s:%s:%s", cacheName, preKey, key);
    }

    /**
     * 生成随机过期时间(误差1s内)
     * @param timeout 缓存超时时间
     * @return
     */
    private static long genRandomTimeout(Long timeout) {
        Random random = new Random();
        return timeout + random.nextInt(1000);
    }
}
