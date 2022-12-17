package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 用户扩展表 PO
 */
public class UserExtraPO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "渠道 支付宝 微信 ")
    private String channel;

    @ApiModelProperty(value = "渠道 扩展字段1")
    private String channelExt1;

    @ApiModelProperty(value = "渠道 扩展字段2")
    private String channelExt2;

    @ApiModelProperty(value = "注册时的ip地址")
    private String registerIp;

    @ApiModelProperty(value = "最后登陆时间")
    private Date lastLoginAt;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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
        return "UserExtraPO{" +
                "id='" + id + '\'' +
                ", country='" + country + '\'' +
                ", channel='" + channel + '\'' +
                ", channelExt1='" + channelExt1 + '\'' +
                ", channelExt2='" + channelExt2 + '\'' +
                ", registerIp='" + registerIp + '\'' +
                ", lastLoginAt=" + lastLoginAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
