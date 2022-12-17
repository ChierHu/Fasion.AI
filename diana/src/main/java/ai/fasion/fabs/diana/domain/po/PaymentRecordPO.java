package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class PaymentRecordPO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "总计")
    private Integer amount;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "渠道")
    private String channel;

    @ApiModelProperty(value = "扩充字段1")
    private String channelExt1;

    @ApiModelProperty(value = "扩充字段2")
    private String channelExt2;

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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelExt1() {
        return channelExt1;
    }

    public void setChannelExt1(String channelExt1) {
        this.channelExt1 = channelExt1;
    }

    public String getChannelExt2() {
        return channelExt2;
    }

    public void setChannelExt2(String channelExt2) {
        this.channelExt2 = channelExt2;
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
        return "PaymentRecordPO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", channel='" + channel + '\'' +
                ", channelExt1='" + channelExt1 + '\'' +
                ", channelExt2='" + channelExt2 + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
