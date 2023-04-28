package cn.gybyt.util;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
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
     * 从缓存中获取数据
     *
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T get(String key){
        return (T)redisTemplate.opsForValue().get(key);

    }

    /**
     * 从缓存中获取哈希类型数据
     *
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T hashGet(String key, Object hashKey){
        return (T)redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 从缓存中获取数据，如果为空使用回调获取数据
     *
     * @param key
     * @param callable
     * @return
     * @param <T>
     */
    public static <T> T get(String key, Callable<T> callable){
        T t = (T) redisTemplate.opsForValue().get(key);
        if (t == null){
            try {
                t = callable.call();
                set(key, t);
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }
        return t;
    }

    /**
     * 从缓存中获取数据，如果为空使用回调获取数据，并指定缓存有效时间
     *
     * @param key
     * @param callable
     * @return
     * @param <T>
     */
    public static <T> T get(String key, Callable<T> callable, Integer time){
        T t = (T) redisTemplate.opsForValue().get(key);
        if (t == null){
            try {
                t = callable.call();
                set(key, t, time);
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }
        return t;
    }

    /**
     * 从缓存中获取哈希类型数据，如果为空使用回调获取数据，并指定缓存有效时间
     *
     * @param key
     * @param callable
     * @return
     * @param <T>
     */
    public static <T> T getHash(String key, Object hashKey, Callable<T> callable, Integer time){
        T t = (T) redisTemplate.opsForHash().get(key, hashKey);
        if (t == null){
            try {
                t = callable.call();
                setHash(key, hashKey, t, time);
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }
        return t;
    }

    /**
     * 从缓存中获取哈希类型数据，如果为空使用回调获取数据，并指定缓存有效时间
     *
     * @param key
     * @return
     */
    public static <T, R> Map<T, R> getHashMap(String key) {
        return (Map<T, R>) redisTemplate.opsForHash().entries(key);
    }

    /**
     * 从缓存中获取哈希类型数据，如果为空使用回调获取数据
     *
     * @param key
     * @param callable
     * @return
     * @param <T>
     */
    public static <T> T getHash(String key, Object hashKey, Callable<T> callable){
        T t = (T) redisTemplate.opsForHash().get(key, hashKey);
        if (t == null){
            try {
                t = callable.call();
                setHash(key, hashKey, t);
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }
        return t;
    }

    /**
     * 设置哈希方式缓存，并指定缓存时间
     * @param key
     * @param object
     * @param time
     */
    public static void setHash(String key, Object hashKey, Object object, Integer time) {
        if (BaseUtil.isNotEmpty(object)) {
            if (object instanceof String) {
                stringRedisTemplate.opsForHash().put(key, hashKey, object);
                stringRedisTemplate.expire(key, genRandomTime(time), TimeUnit.MILLISECONDS);
            }
            redisTemplate.opsForHash().put(key, hashKey, object);
            redisTemplate.expire(key, genRandomTime(time), TimeUnit.MILLISECONDS);

        }
    }

    /**
     * 设置哈希方式批量缓存，并指定缓存时间
     * @param key
     * @param map
     * @param time
     */
    public static void setHash(String key, Map map, Integer time) {
        if (BaseUtil.isNotEmpty(map)) {
            redisTemplate.opsForHash().putAll(key, map);
            redisTemplate.expire(key, genRandomTime(time), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 设置哈希方式缓存
     * @param key
     * @param object
     */
    public static void setHash(String key, Object hashKey, Object object) {
        if (BaseUtil.isNotEmpty(object)) {
            if (object instanceof String) {
                stringRedisTemplate.opsForHash().put(key, hashKey, object);
            }
            redisTemplate.opsForHash().put(key, hashKey, object);
        }
    }

    /**
     * 设置哈希方式批量缓存
     * @param key
     * @param map
     */
    public static void setHash(String key, Map map) {
        if (BaseUtil.isNotEmpty(map)) {
            redisTemplate.opsForHash().putAll(key, map);
        }
    }

    /**
     * 从缓存中获取哈希类型数据
     *
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getHash(String key, Object hashKey){
        return (T)redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param object
     */
    public static void set(String key, Object object){
        if (BaseUtil.isNotEmpty(object)) {
            if (object instanceof String) {
                stringRedisTemplate.opsForValue().set(key, object);
            }
            redisTemplate.opsForValue().set(key, object);
        }
    }

    /**
     * 设置缓存，并指定缓存有效时间
     *
     * @param key
     * @param object
     * @param time
     */
    public static void set(String key, Object object, Integer time){
        if (object instanceof String) {
            stringRedisTemplate.opsForValue().set(key, object, genRandomTime(time), TimeUnit.MILLISECONDS);
        }
        redisTemplate.opsForValue().set(key, object, genRandomTime(time), TimeUnit.MILLISECONDS);
    }

    /**
     * 删除缓存
     * @param key
     */
    public static void remove(String key){
        redisTemplate.delete(key);
    }

    /**
     * 删除哈希缓存
     * @param key
     * @param hashKey
     */
    public static void remove(String key, Object hashKey){
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 根据集合删除哈希缓存
     * @param keys
     */
    public static void remove(String key, Collection<Object> keys){
        redisTemplate.opsForHash().delete(key, keys);
    }

    /**
     * 根据集合删除缓存
     * @param keys
     */
    public static void remove(Collection<String> keys){
        redisTemplate.delete(keys);
    }

    /**
     * 判断是否有缓存
     * @param key
     * @return
     */
    public static Boolean hasKay(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断hash中是否有缓存
     * @param key
     * @param hashKey
     * @return
     */
    public static Boolean hasHashKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 生成随机过期时间
     * @param minutes
     * @return
     */
    private static Long genRandomTime(Integer minutes) {
        Random random = new Random();
        return minutes * 1000L + random.nextInt(1000);
    }
}
