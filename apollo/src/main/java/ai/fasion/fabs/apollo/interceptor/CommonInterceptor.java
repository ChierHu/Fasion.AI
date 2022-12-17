package ai.fasion.fabs.apollo.interceptor;

import ai.fasion.fabs.vesta.service.context.EquipmentThreadLocalHolder;
import ai.fasion.fabs.vesta.service.context.ThreadLocalHolder;
import ai.fasion.fabs.vesta.domain.pojo.EquipmentInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Function: 获取用户设备信息
 *
 * @author miluo
 * Date: 2021/3/5 10:32
 * @since JDK 1.8
 */
@Component
public class CommonInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CommonInterceptor.class);

    private final ObjectMapper objectMapper;

    public CommonInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 请求前先过拦截器
     * 在请求处理之前执行，主要用于权限验证、参数过滤等
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 获取请求头中设备信息
        String authentication = request.getHeader("EquipmentInformation");
        if (null != authentication) {
            String json = new String(Base64.getDecoder().decode(authentication), StandardCharsets.UTF_8);
            System.out.println("收到的设备信息：" + json);
            try {
                EquipmentInfo equipmentInfo = objectMapper.readValue(json, EquipmentInfo.class);
                EquipmentThreadLocalHolder.setEquipmentInfo(equipmentInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ThreadLocalHolder.setRequest(request);
        ThreadLocalHolder.setResponse(response);
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
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        // 清除threadLocal
        ThreadLocalHolder.clean();
        EquipmentThreadLocalHolder.clean();
    }
}
