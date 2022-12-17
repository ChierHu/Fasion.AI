package ai.fasion.fabs.minerva.config

import ai.fasion.fabs.vesta.enums.Task
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
class MinervaConfig {

    @Bean
    fun taskRedisTemplate(redisConnectionFactory: RedisConnectionFactory) =
        RedisTemplate<String, Task>().apply {
            @Suppress("UsePropertyAccessSyntax")
            setConnectionFactory(redisConnectionFactory)

            valueSerializer = GenericJackson2JsonRedisSerializer(
                ObjectMapper()
                    .registerModule(KotlinModule())
                    .registerModule(JavaTimeModule())
                    .configure(MapperFeature.USE_ANNOTATIONS, false)
                    .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                            .allowIfBaseType(Any::class.java)
                            .build(),
                        ObjectMapper.DefaultTyping.EVERYTHING,
                        JsonTypeInfo.As.PROPERTY
                    )
            )
            keySerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()

            afterPropertiesSet()
        }

}

