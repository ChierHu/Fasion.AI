package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * api申请 PO
 */
public class ApiApplyPO {

    @ApiModelProperty(value = "api申请id")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "申请人的申请说明")
    private String reqNote;

    @ApiModelProperty(value = "审批时的审批说明")
    private String reviewNote;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReqNote() {
        return reqNote;
    }

    public void setReqNote(String reqNote) {
        this.reqNote = reqNote;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
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
        return "ApiApplyPO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", status=" + status +
                ", reqNote='" + reqNote + '\'' +
                ", reviewNote='" + reviewNote + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
