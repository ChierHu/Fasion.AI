package ai.fasion.fabs.vesta.service.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Function: threadLocal request response
 * @author yangzhiyuan Date: 2021-01-27 18:12:45
 * @since JDK 1.8
 */
public class ThreadLocalHolder {

    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();

    public static HttpServletRequest getRequest() {
        return request.get();
    }

    public static void setRequest(HttpServletRequest request) {
        ThreadLocalHolder.request.set(request);
    }

    public static HttpServletResponse getResponse() {
        return response.get();
    }

    public static void setResponse(HttpServletResponse response) {
        ThreadLocalHolder.response.set(response);
    }

    public static void clean() {
        request.remove();
        response.remove();
    }
}
