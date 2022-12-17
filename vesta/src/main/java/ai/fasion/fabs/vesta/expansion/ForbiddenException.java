package ai.fasion.fabs.vesta.expansion;

/**
 * Function: http 403
 * 当前用户登陆成功，但是拿的资源不是自己的，没有权限
 *
 * @author miluo
 * Date: 2021/2/24 10:16
 * @since JDK 1.8
 */
public class ForbiddenException extends RuntimeException {
    /**
     * 错误编码
     */
    private int errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
        this.errCode = 4010;
        this.errMsg = errMsg;
    }

    public ForbiddenException(int errCode, String errMsg) {
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
