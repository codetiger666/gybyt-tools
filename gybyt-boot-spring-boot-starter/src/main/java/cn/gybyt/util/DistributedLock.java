package cn.gybyt.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * 分布式锁
 *
 * @author: codetiger
 **/
@Slf4j
public class DistributedLock {

    private static RedisTemplate<String, String> REDIS_TEMPLATE;

    static {
        REDIS_TEMPLATE = SpringUtil.getBean(RedisTemplate.class, "gybytRedisStringTemplate");
    }

    /**
     * 默认等待时间（秒）
     */
    private long waitTime = 3L;

    /**
     * 默认持有锁时间（秒）
     */
    private long expireTime = 6L;

    /**
     * 自旋等待间隔（毫秒）
     */
    private static final long DEFAULT_INTERVAL = 200L;

    /**
     * 锁 key
     */
    private final String lockKey;

    /**
     * 锁 value
     */
    private final String value;


    public DistributedLock(String lockKey) {
        this(lockKey, UUID.randomUUID()
                          .toString());
    }

    public DistributedLock(String lockKey, String value) {
        this.lockKey = lockKey;
        this.value = value;
    }

    public DistributedLock(String lockKey,
                     String value,
                     long waitTime,
                     long expireTime) {
        this(lockKey, value);
        this.waitTime = waitTime;
        this.expireTime = expireTime;
    }

    public DistributedLock(String lockKey,
                     long waitTime,
                     long expireTime) {
        this(lockKey);
        this.waitTime = waitTime;
        this.expireTime = expireTime;
    }

    /**
     * 默认等待时间尝试加锁
     *
     * @return 是否加锁成功
     */
    public boolean lock() {
        return lock(this.waitTime);
    }

    /**
     * 自旋方式尝试加锁
     *
     * @param timeoutSeconds 最大等待时间（秒）
     * @return 是否加锁成功
     */
    public boolean lock(long timeoutSeconds) {
        long maxWaitMs = timeoutSeconds * 1000;
        long waited = 0;

        while (waited <= maxWaitMs) {
            if (tryLock(expireTime)) {
                log.debug("获取到锁：{}", lockKey);
                return true;
            }
            log.debug("开始等待获取锁：{}", lockKey);
            try {
                Thread.sleep(DEFAULT_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread()
                      .interrupt();
                log.error("等待获取锁失败", e);
                return false;
            }

            waited += DEFAULT_INTERVAL;
        }
        log.warn("等待获取锁超时：{}", lockKey);
        return false;
    }

    /**
     * 执行加锁逻辑（使用 SET NX EX）
     *
     * @param ttlSeconds 锁过期时间（秒）
     * @return 是否加锁成功
     */
    private boolean tryLock(long ttlSeconds) {
        String script =
                "return redis.call('set', KEYS[1], ARGV[1], 'NX', 'EX', ARGV[2]) and 1 or 0";

        List<String> args = Arrays.asList(value, String.valueOf(ttlSeconds));

        Long ret = eval(script, lockKey, args);
        return ret != null && ret == 1;
    }

    /**
     * 释放锁。只有自己持有的锁才能删除。
     */
    public void unlock() {
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) else return 0 end";

        try {
            eval(script, lockKey, Collections.singletonList(value));
        } catch (Exception e) {
            log.error("释放锁失败", e);
        }
    }

    /**
     * lua脚本完成自减，自减数量 num 必须小于当前值
     *
     * @param redisKey redis key
     * @param num      自减数量
     * @return 是否成功
     */
    public boolean decrIfEnough(String redisKey, int num) {
        String script =
                "local v=redis.call('get',KEYS[1]) " +
                        "if tonumber(v)>=tonumber(ARGV[1]) then " +
                        "   redis.call('incrby',KEYS[1],0-tonumber(ARGV[1])); return 1 " +
                        "else return 0 end";
        Long ret = eval(script, redisKey, Collections.singletonList(String.valueOf(num)));
        return ret != null && ret == 1;
    }

    /**
     * lua脚本完成自增
     *
     * @param redisKey redis key
     * @param num      自增数量
     * @return 是否成功
     */
    public boolean incr(String redisKey, int num) {
        String script =
                "if redis.call('get', KEYS[1]) then " +
                        "redis.call('incrby', KEYS[1], ARGV[1]); return 1 " +
                        "else return 0 end";

        Long ret = eval(script, redisKey, Collections.singletonList(String.valueOf(num)));
        return ret != null && ret == 1;
    }

    /**
     * lua脚本完成自增并返回最新值
     *
     * @param redisKey redis key
     * @param num      自增数量
     * @return 自增后的值
     */
    public Long incrAndReturn(String redisKey, int num) {
        String script =
                "local v=redis.call('GET', KEYS[1]) " +
                        "if v==false then redis.call('SET', KEYS[1], 0) end " +
                        "return redis.call('INCRBY', KEYS[1], ARGV[1])";

        return eval(script, redisKey, Collections.singletonList(String.valueOf(num)));
    }

    /**
     * lua脚本 set 值并设置过期时间
     *
     * @param redisKey redis key
     * @param val      value
     * @param ttl      过期秒数
     * @return 是否成功
     */
    public boolean setWithExpire(String redisKey, String val, long ttl) {
        String script =
                "redis.call('SET', KEYS[1], ARGV[1]); " +
                        "redis.call('EXPIRE', KEYS[1], ARGV[2]); return 1";

        Long ret = eval(script, redisKey, Arrays.asList(val, String.valueOf(ttl)));
        return ret != null && ret == 1;
    }

    /**
     * 执行 lua 脚本（兼容 Jedis / JedisCluster / Lettuce）
     *
     * @param script lua 脚本内容
     * @param key    redis key
     * @param args   参数数组
     * @return 执行结果
     */
    private Long eval(String script, String key, List<String> args) {
        List<String> argsList = new ArrayList<>();
        argsList.add(key);
        argsList.addAll(args);
        return REDIS_TEMPLATE.execute((RedisCallback<Long>) conn -> conn.eval(
                script.getBytes(),
                org.springframework.data.redis.connection.ReturnType.INTEGER,
                1,
                argsList.stream()
                        .map(String::getBytes)
                        .toArray(byte[][]::new)
        ));
    }

}
