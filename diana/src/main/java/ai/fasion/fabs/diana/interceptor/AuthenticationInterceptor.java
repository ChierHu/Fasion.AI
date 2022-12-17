package ai.fasion.fabs.diana.interceptor;

import ai.fasion.fabs.diana.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Function: 认证拦截器
 * @since JDK 1.8
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Autowired
    private AdminUserService backstageAdminUserInfoService;

    /**
     * 在请求处理之前执行，主要用于权限验证、参数过滤等
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("登陆认证，地址来源：[{}]", request.getRequestURI());
        // 获取请求头中auth信息
        String authentication = request.getHeader("Authorization");
        // 校验用户是否登录
        return backstageAdminUserInfoService.authentication(authentication);
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
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

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
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("清除threadLocal，地址来源：[{}]", request.getRequestURI());
        // 清除threadLocal
        AppThreadLocalHolder.clean();
    }
}
