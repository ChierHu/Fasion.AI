package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class UserInfoPO {

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "用户手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "邮箱状态 1--激活  2-- 待激活 ")
    private Integer emailStatus;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像url")
    private String avatar;

    @ApiModelProperty(value = "状态 1--启用  2--禁用 ")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;

    @ApiModelProperty(value = "用户申请资料")
    private String  meta;

    @ApiModelProperty("用户名字")
    private String name;

    @ApiModelProperty("公司名字")
    private String company;

    @ApiModelProperty("备注")
    private String reqNote;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getReqNote() {
        return reqNote;
    }

    public void setReqNote(String reqNote) {
        this.reqNote = reqNote;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserInfoPO{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", emailStatus=" + emailStatus +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", meta='" + meta + '\'' +
                ", name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", reqNote='" + reqNote + '\'' +
                '}';
    }
}
