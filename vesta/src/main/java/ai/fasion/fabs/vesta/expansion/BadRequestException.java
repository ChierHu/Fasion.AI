package ai.fasion.fabs.vesta.expansion;

/**
 * Function: http 400
 * 用户请求错误
 *
 * @author miluo
 * Date: 2021/2/24 10:16
 * @since JDK 1.8
 */
public class BadRequestException extends RuntimeException {
    /**
     * 错误编码
     */
    private int errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    public BadRequestException() {
        super();
    }

    public BadRequestException(String message) {
        super(message);
        this.errCode = 4010;
        this.errMsg = errMsg;
    }

    public BadRequestException(int errCode, String errMsg) {
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
