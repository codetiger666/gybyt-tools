package cn.gybyt.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

/**
 * redis配置
 *
 * @program: utils
 * @classname: RedisConfig
 * @author: codetiger
 * @create: 2022/11/9 19:39
 **/
@Configuration
@ConditionalOnClass({RedisConnectionFactory.class})
@ConditionalOnProperty(prefix = "gybyt", name = "enable-cache", havingValue = "true")
public class RedisConfig {

    /**
     * 注入RedisTemplate对象
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> gybytRedisTemplate(RedisConnectionFactory factory) {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                                 JsonTypeInfo.As.PROPERTY);
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(om));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(om));
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 注入RedisTemplate对象
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, String> gybytRedisStringTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 注入RedisCacheManage对象
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public CacheManager gybytRedisCacheManager(
            @Qualifier(value = "gybytRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                                 JsonTypeInfo.As.PROPERTY);
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(om)))
                .entryTtl(Duration.ofHours(1L));
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }


}
