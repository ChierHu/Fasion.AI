package ai.fasion.fabs.diana.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class UserInfoDTO {

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户手机号")
    private String phone;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "状态 banned--禁用  active--正常  pending--审核  created--创建 ")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;

    @ApiModelProperty(value = "用户名字")
    private String name;

    @ApiModelProperty(value = "公司名字")
    private String company;

    @ApiModelProperty(value = "申请提交备注")
    private String reqNote;

    @ApiModelProperty(value = "申请详情")
    private String meta;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    @Override
    public String toString() {
        return "UserInfoDTO{" +
                "uid='" + uid + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", reqNote='" + reqNote + '\'' +
                ", meta='" + meta + '\'' +
                '}';
    }
}
