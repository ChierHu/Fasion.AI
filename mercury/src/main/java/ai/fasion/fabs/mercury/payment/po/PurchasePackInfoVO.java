package ai.fasion.fabs.mercury.payment.po;

import java.util.Arrays;
import java.util.Date;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/9/18 14:18
 * @since JDK 1.8
 */
public class PurchasePackInfoVO {

    /**
     * 订单id
     */
    private String id;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 资金表id
     */
    private String[] payments;

    /**
     * sku表id
     */
    private String skuId;

    /**
     * 数量
     */
    private int amount;

    /**
     * task表id 或 payment表id
     */
    private String productId;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 点数
     */
    private Integer points;

    /**
     * 过期天数
     */
    private Integer expirationPeriod;

    /**
     * 套餐类型
     */
    private String type;

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

    public String[] getPayments() {
        return payments;
    }

    public void setPayments(String[] payments) {
        this.payments = payments;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getExpirationPeriod() {
        return expirationPeriod;
    }

    public void setExpirationPeriod(Integer expirationPeriod) {
        this.expirationPeriod = expirationPeriod;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PurchasePackInfoVO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", payments=" + Arrays.toString(payments) +
                ", skuId='" + skuId + '\'' +
                ", amount=" + amount +
                ", productId='" + productId + '\'' +
                ", createdAt=" + createdAt +
                ", points=" + points +
                ", expirationPeriod=" + expirationPeriod +
                ", type='" + type + '\'' +
                '}';
    }
}
