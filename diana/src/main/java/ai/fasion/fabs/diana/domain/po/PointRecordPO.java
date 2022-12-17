package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


/**
 * 点数记录 PO
 */
public class PointRecordPO {

    @ApiModelProperty(value = "点数记录Id")
    private String id;

    @ApiModelProperty(value = "事务id")
    private String tid;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "金额")
    private Integer amount;

    @ApiModelProperty(value = "操作类型")
    private Integer opType;

    @ApiModelProperty(value = "扩展字段pay_id")
    private String opExt1;

    @ApiModelProperty(value = "扩展字段sku_id")
    private String opExt2;

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

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public String getOpExt1() {
        return opExt1;
    }

    public void setOpExt1(String opExt1) {
        this.opExt1 = opExt1;
    }

    public String getOpExt2() {
        return opExt2;
    }

    public void setOpExt2(String opExt2) {
        this.opExt2 = opExt2;
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
        return "PointRecordPO{" +
                "id='" + id + '\'' +
                ", tid='" + tid + '\'' +
                ", uid='" + uid + '\'' +
                ", amount=" + amount +
                ", opType=" + opType +
                ", opExt1='" + opExt1 + '\'' +
                ", opExt2='" + opExt2 + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
