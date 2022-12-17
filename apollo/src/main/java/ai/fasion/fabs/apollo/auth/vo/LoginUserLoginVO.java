package ai.fasion.fabs.apollo.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Pattern;

/**
 * Function: 登录信息VO
 *
 * @author yangzhiyuan Date: 2021-01-25 18:18:52
 * @since JDK 1.8
 */
@ApiModel(value="登录信息",description="登录信息login")
public class LoginUserLoginVO {

  @ApiModelProperty(value = "手机号")
  @Pattern(regexp = "^\\d{11}$", message = "手机号码格式错误")
  private String phone;

  @ApiModelProperty(value = "短信验证码")
  @Pattern(regexp = "^[0-9]{4}$", message = "短信验证码错误")
  private String code;

  @ApiModelProperty(value = "密码")
  private String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return "LoginUserLoginVO{" +
            "phone='" + phone + '\'' +
            ", code='" + code + '\'' +
            ", password='" + password + '\'' +
            '}';
  }
}
