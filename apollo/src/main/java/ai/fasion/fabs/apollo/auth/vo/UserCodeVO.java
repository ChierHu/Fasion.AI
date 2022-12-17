package ai.fasion.fabs.apollo.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;

/**
 * Function:发送短信验证码实体类
 *
 * @author miluo
 * Date: 2021/2/20 15:22
 * @since JDK 1.8
 */
@ApiModel(value = "发送验证码", description = "登录信息login")
public class UserCodeVO {
    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^\\d{11}$", message = "手机号码格式错误")
    private String phone;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UmsUserCodeVO{" +
                "phone='" + phone + '\'' +
                '}';
    }
}
