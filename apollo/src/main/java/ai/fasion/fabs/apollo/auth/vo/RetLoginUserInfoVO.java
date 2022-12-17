package ai.fasion.fabs.apollo.auth.vo;

import io.swagger.annotations.ApiModelProperty;


/**
 * Function: 登陆成功后返回用户信息
 *
 * @author miluo
 * Date: 2021/5/25 17:09
 * @since JDK 1.8
 */
public class RetLoginUserInfoVO {

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String uid;

    @ApiModelProperty("用户名")
    private String nickname;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("用户状态")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "RetLoginUserInfoVO{" +
                "uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", token='" + token + '\'' +
                ", avatar='" + avatar + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
