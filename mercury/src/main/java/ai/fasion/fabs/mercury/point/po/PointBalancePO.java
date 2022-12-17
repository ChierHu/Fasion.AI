package ai.fasion.fabs.mercury.point.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 点数表
 */
public class PointBalancePO {

    /**
     * 点数id
     */
    private String id;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 点数
     */
    private Integer amount;

    /**
     * 消费/花费
     */
    private Integer spending;

    /**
     * 直接记录到期时间
     */
    private Date expiredAt;

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

    public Integer getSpending() {
        return spending;
    }

    public void setSpending(Integer spending) {
        this.spending = spending;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Override
    public String toString() {
        return "PointBalancePO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", amount=" + amount +
                ", spending=" + spending +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
