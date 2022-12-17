package ai.fasion.fabs.apollo.interceptor;

import ai.fasion.fabs.apollo.auth.AuthService;
import ai.fasion.fabs.apollo.config.annotation.IgnoreUserStatus;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Function: 认证拦截器
 *
 * @author yangzhiyuan Date: 2021-01-27 18:12:45
 * @since JDK 1.8
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private final AuthService authService;

    public AuthenticationInterceptor(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 在请求处理之前执行，主要用于权限验证、参数过滤等
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        log.info("登陆认证，地址来源：[{}]", request.getRequestURI());
        // 获取请求头中auth信息
        String token = request.getHeader("Authorization");
        // 校验用户是否登录
        UserInfoDO userInfo = authService.authentication(token);
        //进一步根据注解判断用户token是否有效
        if (parseMethod(handler)) {
            authService.judgeUserStatus(userInfo);
        }
        return true;
    }

    /**
     * 当前请求进行处理之后执行，主要用于日志记录、权限检查、性能监控、通用行为等
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    /**
     * 当前对应的interceptor的perHandle方法的返回值为true时,postHandle执行完成并渲染页面后执行，主要用于资源清理工作
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        log.info("清除threadLocal，地址来源：[{}]", request.getRequestURI());
        // 清除threadLocal
        AppThreadLocalHolder.clean();
    }

    /**
     * 判断当前方法是否需要判断用户状态信息
     *
     * @param handler springboot HandlerMethod
     * @return true->
     */
    private static boolean parseMethod(Object handler) {
        // 将handler强转为HandlerMethod
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 从方法处理器中获取出要调用的方法
        Method method = handlerMethod.getMethod();
        // 获取出方法上的IgnoreUserStatus注解
        IgnoreUserStatus ignore = method.getAnnotation(IgnoreUserStatus.class);
        // 如果注解不为null, 说明不需要判断用户状态信息，因此设置为true
        return null == ignore;
    }
}
