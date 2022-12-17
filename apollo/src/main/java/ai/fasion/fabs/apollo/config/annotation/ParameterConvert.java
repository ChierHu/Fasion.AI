package ai.fasion.fabs.apollo.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Function: 定义一个下划线要转驼峰的注解
 *
 * @author miluo
 * Date: 2021/9/8 20:36
 * @since JDK 1.8
 */
@Target(value = {ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterConvert {
}
