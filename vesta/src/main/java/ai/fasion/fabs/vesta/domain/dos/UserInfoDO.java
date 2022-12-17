package ai.fasion.fabs.vesta.domain.dos;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * Function: 用户DO
 *
 * @author yangzhiyuan Date: 2021-01-21 10:35:16
 * @since JDK 1.8
 */
public class UserInfoDO implements Serializable {

    private static final long serialVersionUID = 3756830882172886970L;

    /**
     * 自增 主键 用户id
     */
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
    @ApiModelProperty(value = "用户状态 0 -> banned 1 -> active 2 -> pending 3 -> created")
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
     * 用户申请试用信息
     * @return
     */
    private  String meta;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserInfoDO{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", emailStatus=" + emailStatus +
                ", status='" + status + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", meta='" + meta + '\'' +
                '}';
    }
}
