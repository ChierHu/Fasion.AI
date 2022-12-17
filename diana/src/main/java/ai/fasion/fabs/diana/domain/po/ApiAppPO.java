package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class ApiAppPO {

    @ApiModelProperty(value = "ApiAppId")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "ApiApp名字")
    private String name;

    @ApiModelProperty(value = "app密钥")
    private String appKey;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
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
        return "ApiAppPO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", appKey='" + appKey + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
