package cn.gybyt.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.ArrayList;
import java.util.List;
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
@SuppressWarnings("unchecked")
public class CacheUtil {

    private static final RedisTemplate redisTemplate;
    private static final RedisTemplate stringRedisTemplate;

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
    public static <T> T get(String cacheName, String preKey, String key, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                set(genKey(cacheName, preKey, key), t, timeout);
            }
        } catch (Exception ignored) {}
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
    public static <T> T get(String cacheName, String preKey, String key, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(genKey(cacheName, preKey, key));
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                set(genKey(cacheName, preKey, key), t, null);
            }
        } catch (Exception ignored) {}
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
    public static <T> T get(String key, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(key);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(key);
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                set(key, t, timeout);
            }
        } catch (Exception ignored) {}
        return t;
    }


    /**
     * 获取缓存
     * @param key 缓存key
     * @param callable 回调方法
     * @return 返回值
     * @param <T> 泛型
     */
    public static <T> T get(String key, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForValue().get(key);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForValue().get(key);
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                set(key, t, null);
            }
        } catch (Exception ignored) {}
        return t;
    }

    /**
     * 获取缓存
     * @param key 缓存key
     * @return 返回值
     * @param <T> 泛型
     */
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
    public static <T> T getHash(String key, Object hashKey, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(key, hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(key, hashKey);
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                setHash(key, hashKey, t, null);
            }
        } catch (Exception ignored) {}
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
    public static <T> T getHash(String key, Object hashKey, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(key, hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(key, hashKey);
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                setHash(key, hashKey, t, timeout);
            }
        } catch (Exception ignored) {}
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
    public static <T> T getHash(String cacheName, String preKey, String key, Object hashKey, Callable<T> callable) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                setHash(key, hashKey, t, null);
            }
        } catch (Exception ignored) {}
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
    public static <T> T getHash(String cacheName, String preKey, String key, Object hashKey, Callable<T> callable, Long timeout) {
        T t;
        try {
            t = (T) redisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        } catch (SerializationException e) {
            t = (T) stringRedisTemplate.opsForHash().get(genKey(cacheName, preKey, key), hashKey);
        }
        try {
            if (BaseUtil.isNull(t)) {
                t = callable.call();
                setHash(key, hashKey, t, timeout);
            }
        } catch (Exception ignored) {}
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
     * 添加缓存(非null值)
     * @param key 缓存key
     * @param o 需缓存的对象
     */
    public static void set(String key, Object o) {
        if (BaseUtil.isNull(o)) {
            return;
        }
        if (o instanceof String) {
            stringRedisTemplate.opsForValue().set(key, o, genRandomTimeout(24 * 60 * 60 * 1000L), TimeUnit.MILLISECONDS);
            return;
        }
        redisTemplate.opsForValue().set(key, o, genRandomTimeout(24 * 60 * 60 * 1000L), TimeUnit.MILLISECONDS);
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
     * 获取hash数量
     * @param key
     * @return
     */
    public static Long getHashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取hash集合
     *
     * @param key
     * @return
     */
    public static <T, R> Map<T, R> getHashMap(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (SerializationException e) {
            return stringRedisTemplate.opsForHash().entries(key);
        }
    }

    /**
     * 添加Hash缓存(非null值)
     * @param key 缓存key
     * @param o 需缓存的对象
     */
    public static void setHash(String key, Object hashKey, Object o) {
        if (BaseUtil.isNull(o)) {
            return;
        }
        redisTemplate.opsForHash().put(key, hashKey, o);
        redisTemplate.expire(key, genRandomTimeout(24 * 60 * 60 * 1000L), TimeUnit.MILLISECONDS);
    }

    /**
     * 添加Hash缓存(非null值)
     * @param key 缓存key
     * @param map 需缓存的对象map
     * @param timeout 超时时间(毫秒)
     */
    public static void setHash(String key, Map<?, ?> map, Long timeout) {
        if (BaseUtil.isNull(map)) {
            return;
        }
        if (BaseUtil.isNull(timeout)) {
            timeout = 24 * 60 * 60 * 1000L;
        }
        redisTemplate.opsForHash().putAll(key, map);
        if (timeout == -1L) {
            return;
        }
        redisTemplate.expire(key, genRandomTimeout(timeout), TimeUnit.MILLISECONDS);
    }

    /**
     * 添加Hash缓存(非null值)
     * @param key 缓存key
     * @param map 需缓存的对象map
     */
    public static void setHash(String key, Map<?, ?> map) {
        if (BaseUtil.isNull(map)) {
            return;
        }
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, genRandomTimeout(24 * 60 * 60 * 1000L), TimeUnit.MILLISECONDS);
    }

    /**
     * 是否存在 key
     * @param key 缓存key
     * @return
     */
    public static Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 是否存在 key
     * @param key 缓存key
     * @return
     */
    public static Boolean hasKey(String cacheName, String preKey, String key) {
        return redisTemplate.hasKey(genKey(cacheName, preKey, key));
    }

    /**
     * 是否存在hash key
     * @param key 缓存key
     * @param hashKey hash key
     * @return
     */
    public static Boolean hasHashKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 是否存在hash key
     * @param cacheName 缓存名称
     * @param preKey 缓存前缀
     * @param key 缓存key
     * @param hashKey hash key
     * @return
     */
    public static Boolean hasHashKey(String cacheName, String preKey, String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(genKey(cacheName, preKey, key), hashKey);
    }

    /**
     * 删除key
     * @param cacheName 缓存名称
     * @param preKey 缓存前缀
     * @param key 缓存key
     */
    public static void remove(String cacheName, String preKey, String key) {
        redisTemplate.delete(genKey(cacheName, preKey, key));
    }

    /**
     * 删除key
     * @param cacheName 缓存名称
     * @param preKey 缓存前缀
     * @param keyList 缓存key列表
     */
    public static void remove(String cacheName, String preKey, List<String> keyList) {
        redisTemplate.delete(genKeyList(cacheName, preKey, keyList));
    }

    /**
     * 删除key
     * @param key 缓存key
     */
    public static void remove(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除key
     * @param key 缓存key
     */
    public static void remove(List<String> key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除Hash key
     * @param key 缓存key
     * @param hashKey hash key
     */
    public static void removeHash(String key, Object hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 删除Hash key
     * @param key 缓存key
     * @param hashKey hash key
     */
    public static void removeHash(String key, List<Object> hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 删除Hash key
     * @param key 缓存key
     * @param hashKey hash key
     * @param cacheName 缓存名称
     * @param preKey 缓存前缀
     */
    public static void removeHash(String cacheName, String preKey, String key, List<Object> hashKey) {
        redisTemplate.opsForHash().delete(genKey(cacheName, preKey, key), hashKey);
    }

    /**
     * 删除Hash key
     * @param key 缓存key
     * @param hashKey hash key
     * @param cacheName 缓存名称
     * @param preKey 缓存前缀
     */
    public static void removeHash(String cacheName, String preKey, String key, Object hashKey) {
        redisTemplate.opsForHash().delete(genKey(cacheName, preKey, key), hashKey);
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
     * 不需要:结尾
     * @param cacheName 缓存名称
     * @param preKey 缓存前缀
     * @param keyList 缓存key列表
     * @return key列表
     */
    private static List<String> genKeyList(String cacheName, String preKey, List<String> keyList) {
        List<String> keys = new ArrayList<>();
        if (BaseUtil.isEmpty(keyList)) {
            return keys;
        }
        keyList.forEach((key) -> keys.add(String.format("%s:%s:%s", cacheName, preKey, key)));
        return keys;
    }

    /**
     * 生成随机过期时间(误差1s内)
     * @param timeout 缓存超时时间
     * @return 过期时间
     */
    private static long genRandomTimeout(Long timeout) {
        Random random = new Random();
        return timeout + random.nextInt(1000);
    }
}
