package ai.fasion.fabs.apollo.config.annotation;

import java.lang.annotation.*;

/**
 * Function: 忽略用户的状态
 * 默认不设置value的话，忽略所有状态，如果设置了，忽略特定状态。
 * eg: active ->只忽略active状态
 *
 * @author miluo
 * Date: 2021/8/13 10:36
 * @since JDK 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreUserStatus {
    String value() default "";
}
