package ai.fasion.fabs.apollo.config;

import ai.fasion.fabs.apollo.interceptor.AuthenticationInterceptor;
import ai.fasion.fabs.apollo.interceptor.CommonAuthenticationInterceptor;
import ai.fasion.fabs.apollo.interceptor.CommonInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Function: 1. 添加静态文件夹，用于展示图片使用
 *
 * @author miluo Date: 2018/10/16 下午2:43
 * @since JDK 1.8
 */
@Configuration
@EnableWebMvc
public class MyWebMvcConfig implements WebMvcConfigurer {
    private final AuthenticationInterceptor authenticationInterceptor;
    private final CommonInterceptor commonInterceptor;
    private final CommonAuthenticationInterceptor commonAuthenticationInterceptor;

    public MyWebMvcConfig(AuthenticationInterceptor authenticationInterceptor, CommonInterceptor commonInterceptor, CommonAuthenticationInterceptor commonAuthenticationInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.commonInterceptor = commonInterceptor;
        this.commonAuthenticationInterceptor = commonAuthenticationInterceptor;
    }

    /**
     * 将自定义的注解添加到springboot链中
     *
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserMethodArgumentResolver());
        resolvers.add(underlineToCamelArgumentResolver());
    }

    /**
     * 添加自定义注解配置
     *
     * @return
     */
    @Bean
    public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new CurrentUserMethodArgumentResolver();
    }

    /**
     * 添加自定义注解配置
     *
     * @return
     */
    @Bean
    public UnderlineToCamelArgumentResolver underlineToCamelArgumentResolver() {
        return new UnderlineToCamelArgumentResolver();
    }


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 设置拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //认证拦截器
        registry
                .addInterceptor(authenticationInterceptor)
                .excludePathPatterns("/auth/code", "/auth/login", "/auth/logout")
                .excludePathPatterns("/swagger-ui/**", "/swagger-resources/**", "/v3/**")
                .addPathPatterns("/**")
                .order(Ordered.LOWEST_PRECEDENCE);


        //通用认证拦截器，对那些可登陆可不登陆的接口进行拦截
        registry
                .addInterceptor(commonAuthenticationInterceptor)
                .addPathPatterns("/material/photo/list")
                .order(Ordered.LOWEST_PRECEDENCE);

        registry
                .addInterceptor(commonInterceptor)
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 添加静态资源--过滤swagger-api (开源的在线API文档) 并添加静态文件夹，用于向外部返回图片地址
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 系统默认路径，需要手动添加
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        // 过滤swagger
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui/")
                .setViewName("forward:/swagger-ui/index.html");
    }

    /**
     * 解决序列化Long类型时到前端丢失精度问题，同时解决日期序列化问题 针对Long丢失经度问题；发现后台返回前台的数据不会出现问题。前台请求的数据也是正常的
     * 只不过前台js获取到页面上会出现问题，针对以上情况，在前台js中，修改id的name值为id_
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();

        ObjectMapper objectMapper = new ObjectMapper();
        // 对于空的对象转json的时候不抛出错误 不显示为null的字段
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 禁用遇到未知属性抛出异常
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 过滤对象的null属性.
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 序列化为默认格式
        //objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 统一返回数据的输出风格 (多个单词返回时不采用驼峰命名法，采用下划线)
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());

        // 忽略transient
        objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        // 序列化BigDecimal时不使用科学计数法输出
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // LocalDateTime系列序列化和反序列化模块，继承自jsr310，在这里修改了日期格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 序列化成字符串
        // javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(
                LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(
                LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // 序列化成时间戳
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());

        // 反序列化成字符串
        // javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(
                LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(
                LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // 序列化成时间戳
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

        objectMapper.registerModule(javaTimeModule);

        // Long类型和BigInteger类型丢失精度问题
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    /**
     * LocalDateTime 序列化实现
     */
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(
                LocalDateTime localDateTime, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (localDateTime != null) {
                long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }

    /**
     * LocalDateTime 反序列化实现
     */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)
                throws IOException {
            long timestamp = p.getValueAsLong();
            if (timestamp > 0) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            } else {
                return null;
            }
        }
    }
}
