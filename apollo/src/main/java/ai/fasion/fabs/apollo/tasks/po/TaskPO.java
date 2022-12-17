package ai.fasion.fabs.apollo.tasks.po;
import ai.fasion.fabs.vesta.enums.Task;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * 任务 PO
 */
public class TaskPO {

    @ApiModelProperty(value = "任务id")
    private String id;

    @ApiModelProperty(value = "物主")
    private String owner;

    @ApiModelProperty(value = "物主id")
    private String ownerId;

    @ApiModelProperty(value = "类型")
    private Task.Type type;

    @ApiModelProperty(value = "状态")
    private Task.Status status;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Task.Type getType() {
        return type;
    }

    public void setType(Task.Type type) {
        this.type = type;
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
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
        return "TaskPO{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerId='" + ownerId + '\'' +
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
