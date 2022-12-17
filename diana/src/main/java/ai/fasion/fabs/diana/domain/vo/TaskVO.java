package ai.fasion.fabs.diana.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

public class TaskVO {
    @ApiModelProperty(value = "任务id")
    private String id;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "状态")
    private String status;

    private List<Object> details;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "物主")
    private String owner;

    @ApiModelProperty(value = "pt_log_id")
    private Long ptLogId;

    @ApiModelProperty(value = "开始时间")
    private Date startedAt;

    @ApiModelProperty(value = "结束时间")
    private Date finishedAt;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getPtLogId() {
        return ptLogId;
    }

    public void setPtLogId(Long ptLogId) {
        this.ptLogId = ptLogId;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Object> getDetails() {
        return details;
    }

    public void setDetails(List<Object> details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskVO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", details=" + details +
                ", uid='" + uid + '\'' +
                ", owner='" + owner + '\'' +
                ", ptLogId='" + ptLogId + '\'' +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
