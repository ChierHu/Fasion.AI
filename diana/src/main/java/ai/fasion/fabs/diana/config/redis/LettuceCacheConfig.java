package ai.fasion.fabs.diana.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Function:使用redis做缓存 继承CachingConfigurerSupport，为了自定义生成KEY的策略。可以不继承 不继承就不能自动生成key
 *
 * @author miluo Date: 2018/10/25 7:31 PM
 * @since JDK 1.8
 */
@Configuration
@EnableCaching
public class LettuceCacheConfig extends CachingConfigurerSupport {
    /**
     * 默认过期时间
     */
    @Value("${diana.redis.expiration}")
    private Long defaultExpiration;

    /**
     * 在方法上使用注解时使用的规则 自定义key. 此方法将会根据完全限定类名+方法名+所有参数的值生成唯一的一个key,即使@Cacheable中的value属性一样，key也会不一样。
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... args) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName()).append("#");
                sb.append(method.getName()).append("(");
                for (Object obj : args) {
                    sb.append(obj.toString()).append(",");
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }

    /**
     * 缓存管理器，在方法上方使用的缓存
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存管理器管理的缓存的默认过期时间
        defaultCacheConfiguration =
                defaultCacheConfiguration
                        .entryTtl(Duration.ofSeconds(defaultExpiration))
                        // 设置 key为string序列化
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        // redis value使用的序列化器
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new KryoRedisSerializer<>(Object.class)))
                        // 不缓存空值
                        .disableCachingNullValues();

        RedisCacheManager.RedisCacheManagerBuilder builder =
                RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
                        .cacheDefaults(defaultCacheConfiguration);
        return builder.build();
    }

    /**
     * redisTemplate序列化配置
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        // 设置值（value）的序列化采用FastJsonRedisSerializer。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
