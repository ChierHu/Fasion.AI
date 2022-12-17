package ai.fasion.fabs.mercury.payment.po;

import java.util.Arrays;
import java.util.Date;

public class PurchasePO {

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
     * 完成数量
     */
    private int shipped;

    /**
     * task表id 或 payment表id
     */
    private String productId;

    /**
     * 状态(pending、succeeded、 cancelled)
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 修改时间
     */
    private Date updatedAt;

    /**
     * 完成时间
     */
    private Date finishedAt;

    public PurchasePO() {
    }

    public PurchasePO(String id, String uid, String[] payments, String skuId, int amount, int shipped, String productId, String status, Date createdAt, Date updatedAt, Date finishedAt) {
        this.id = id;
        this.uid = uid;
        this.payments = payments;
        this.skuId = skuId;
        this.amount = amount;
        this.shipped = shipped;
        this.productId = productId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.finishedAt = finishedAt;
    }

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

    public int getShipped() {
        return shipped;
    }

    public void setShipped(int shipped) {
        this.shipped = shipped;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Override
    public String toString() {
        return "PurchasePO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", payments=" + Arrays.toString(payments) +
                ", skuId='" + skuId + '\'' +
                ", amount=" + amount +
                ", shipped=" + shipped +
                ", productId='" + productId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", finishedAt=" + finishedAt +
                '}';
    }
}
