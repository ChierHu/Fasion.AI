package ai.fasion.fabs.apollo.config;

import ai.fasion.fabs.apollo.config.annotation.CurrentUserInfo;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Function: 自定义当前用户信息注解
 *
 * @author miluo
 * Date: 2021/6/4 17:33
 * @since JDK 1.8
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(UserInfoDO.class)
                && methodParameter.hasParameterAnnotation(CurrentUserInfo.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, @NotNull NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)  {
        return AppThreadLocalHolder.getUserInfo();
    }
}
