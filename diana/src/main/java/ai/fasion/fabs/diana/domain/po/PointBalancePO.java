package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 点数余额 PO
 */
public class PointBalancePO {

    @ApiModelProperty(value = "点数余额id")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "余额")
    private Integer balance;

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

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
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
        return "PointBalancePO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
