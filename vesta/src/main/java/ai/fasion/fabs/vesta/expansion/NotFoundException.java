package ai.fasion.fabs.vesta.expansion;

import ai.fasion.fabs.vesta.common.ResultCode;

/**
 * Function:未发现异常
 *
 * @author miluo
 * Date: 2021/7/6 16:02
 * @since JDK 1.8
 */
public class NotFoundException extends RuntimeException {

    /**
     * 错误编码
     */
    private ResultCode errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
        this.errMsg = errMsg;
    }

    public NotFoundException(ResultCode errCode, String errMsg) {
        super();
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ResultCode getErrCode() {
        return errCode;
    }

    public void setErrCode(ResultCode errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
