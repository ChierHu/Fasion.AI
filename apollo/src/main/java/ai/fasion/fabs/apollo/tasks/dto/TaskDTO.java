package ai.fasion.fabs.apollo.tasks.dto;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class TaskDTO {

    @ApiModelProperty(value = "任务表id")
    private Long id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "物主")
    private String owner;

    @ApiModelProperty(value = "物主id")
    private Long ownerId;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "有效载荷")
    private String payload;

    @ApiModelProperty(value = "状态详情")
    private String details;

    @ApiModelProperty(value = "开始时间")
    private Date startedAt;

    @ApiModelProperty(value = "结束时间")
    private Date finishedAt;

    @ApiModelProperty(value = "日志id")
    private Long ptLogId;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public Long getPtLogId() {
        return ptLogId;
    }

    public void setPtLogId(Long ptLogId) {
        this.ptLogId = ptLogId;
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
        return "TaskDTO{" +
                "id=" + id +
                ", taskId='" + taskId + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerId=" + ownerId +
                ", type=" + type +
                ", status=" + status +
                ", payload='" + payload + '\'' +
                ", details='" + details + '\'' +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", ptLogId=" + ptLogId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
