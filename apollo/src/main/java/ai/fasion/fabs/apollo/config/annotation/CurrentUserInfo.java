package ai.fasion.fabs.apollo.config.annotation;

import java.lang.annotation.*;

/**
 * Function: 自定义当前用户注解
 * 当用户登陆后，可以通过 @CurrentUserInfo 得到当前用户的用户信息
 * 当前元注解需要配合config包下的CurrentUserMethodArgument类使用
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
