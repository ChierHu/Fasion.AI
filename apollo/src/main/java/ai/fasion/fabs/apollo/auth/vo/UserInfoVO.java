package ai.fasion.fabs.apollo.auth.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * Function: 用户信息
 *
 * @author miluo Date: 2018/9/6 下午4:28
 * @since JDK 1.8
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "user信息", description = "用户信息")
public class UserInfoVO {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String id;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;


    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 邮箱状态
     */
    @ApiModelProperty(value = "邮箱状态")
    private Integer emailStatus;

    /**
     * 状态 0:禁用 1: 启用
     */
    @ApiModelProperty(value = "状态 0 -> Banned 1 -> Active 2 -> Pending 3 -> Created")
    private String status;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    /**
     * 别名
     */
    @ApiModelProperty("别名")
    private String nickname;

    /**
     * 密码
     */
    private String password;


    /**
     * token
     */
    private String token;

    /**
     * 用户信息扩展字段
     */
    @ApiModelProperty("用户信息扩展字段")
    private MetaVO meta;

    public MetaVO getMeta() {
        return meta;
    }

    public void setMeta(MetaVO meta) {
        this.meta = meta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserInfoVO{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", emailStatus=" + emailStatus +
                ", status='" + status + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", meta=" + meta +
                '}';
    }
}
