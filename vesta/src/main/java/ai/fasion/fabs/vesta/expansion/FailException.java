package ai.fasion.fabs.vesta.expansion;

import ai.fasion.fabs.vesta.common.ResultCode;

/**
 * Function: 自定义错误信息
 *
 * @author miluo Date: 2018/11/13 5:58 PM
 * @since JDK 1.8
 */
public class FailException extends RuntimeException {

  /** 错误编码 */
  private ResultCode errCode;

  /** 错误消息 */
  private String errMsg;

  public FailException() {
    super();
  }

  public FailException(String message) {
    super(message);
    this.errMsg = errMsg;
  }

  public FailException(ResultCode errCode, String errMsg) {
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
