package ai.fasion.fabs.apollo.tasks.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/29 13:44
 * @since JDK 1.8
 */
public class TaskVO {
    @ApiModelProperty(value = "任务id")
    private String id;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "任务开始时间")
    private Date createdAt;

    private Object details;

    private Object payload;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
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

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "TaskVO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", details=" + details +
                ", payload=" + payload +
                '}';
    }
}
