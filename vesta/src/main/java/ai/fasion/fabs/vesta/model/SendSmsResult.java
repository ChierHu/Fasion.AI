package ai.fasion.fabs.vesta.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Function: 短信发送结果
 *
 * @author yangzhiyuan Date: 2021-01-21 14:17:16
 * @since JDK 1.8
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SendSmsResult {

  /** 200:成功；其它均代表失败 */
  private Integer code;

  @JsonProperty("RequestId")
  private String requestId;

  @JsonProperty("Sid")
  private String sid;

  @JsonProperty("ExtId")
  private String extId;

  @JsonProperty("Error")
  private Error error;

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getSid() {
    return sid;
  }

  public void setSid(String sid) {
    this.sid = sid;
  }

  public String getExtId() {
    return extId;
  }

  public void setExtId(String extId) {
    this.extId = extId;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  public static class Error {

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Message")
    private String message;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
