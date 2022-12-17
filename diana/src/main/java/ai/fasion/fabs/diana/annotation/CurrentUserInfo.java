package ai.fasion.fabs.diana.annotation;

import java.lang.annotation.*;

/**
 * Function: 自定义当前用户注解
 *
 * @author miluo
 * Date: 2021/6/4 17:31
 * @since JDK 1.8
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUserInfo {
    String value() default "";
}
