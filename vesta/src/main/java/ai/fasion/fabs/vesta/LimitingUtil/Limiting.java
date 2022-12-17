package ai.fasion.fabs.vesta.LimitingUtil;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * Function: 限流注解
 *
 * @author miluo
 * Date: 2020/4/23 8:29 下午
 * @since JDK 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
//最高优先级
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface Limiting {
    /**
     * 允许访问的次数
     */
    int count() default 60;

    /**
     * 时间段，多少时间段内运行访问count次
     * 单位秒
     */
    long time() default 600;

}
