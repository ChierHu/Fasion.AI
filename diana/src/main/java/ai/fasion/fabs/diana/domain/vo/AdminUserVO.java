package ai.fasion.fabs.diana.domain.vo;

import io.swagger.annotations.ApiModelProperty;

public class AdminUserVO {

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value ="用户头像url")
    private String avatar;

    @ApiModelProperty(value = "用户状态")
    private String status;

    @ApiModelProperty(value = "token")
    private String token;

    public String getToken() {
        return token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "AdminUserVO{" +
                "uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
