package ai.fasion.fabs.diana.domain.po;

import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.enums.Status;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 资产表
 */
public class AssetPO {

    @ApiModelProperty(value = "资产id")
    private String id;

    @ApiModelProperty(value = "物主")
    private String owner;

    @ApiModelProperty(value = "物主id")
    private String ownerId;

    @ApiModelProperty(value = "类型")
    private Asset.Type type;

    @ApiModelProperty(value = "ticket_id, task_id, ...")
    private String bundle;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "状态")
    private Status.Type status;

    @ApiModelProperty(value = "最后访问时间")
    private Date lastAccessAt;

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

    public Asset.Type getType() {
        return type;
    }

    public void setType(Asset.Type type) {
        this.type = type;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Status.Type getStatus() {
        return status;
    }

    public void setStatus(Status.Type status) {
        this.status = status;
    }

    public Date getLastAccessAt() {
        return lastAccessAt;
    }

    public void setLastAccessAt(Date lastAccessAt) {
        this.lastAccessAt = lastAccessAt;
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
        return "AssetPO{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", type=" + type +
                ", bundle='" + bundle + '\'' +
                ", path='" + path + '\'' +
                ", status=" + status +
                ", lastAccessAt=" + lastAccessAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
