package ai.fasion.fabs.vesta.expansion;

/**
 * Function: http 401
 * 用户没有登陆，没有权限请求数据
 *
 * @author miluo
 * Date: 2021/2/24 10:16
 * @since JDK 1.8
 */
public class AuthorizationException extends RuntimeException {
    /**
     * 错误编码
     */
    private int errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String message) {
        super(message);
        this.errCode = 4010;
        this.errMsg = errMsg;
    }

    public AuthorizationException(int errCode, String errMsg) {
        super();
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
