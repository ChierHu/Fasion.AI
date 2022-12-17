package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PurchasePO {

    @ApiModelProperty(value = "订单id")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "资金表id")
    private String[] payments;

    @ApiModelProperty(value = "sku表id")
    private String skuId;

    @ApiModelProperty(value = "数量")
    private Integer amount;

    @ApiModelProperty(value = "完成数量")
    private Integer shipped;

    @ApiModelProperty(value = "task表id 或 payment表id")
    private String productId;

    @ApiModelProperty(value = "状态(pending、succeed、 cancelled)")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;

    @ApiModelProperty(value = "完成时间")
    private Date finishedAt;

    public Integer getShipped() {
        return shipped;
    }

    public void setShipped(Integer shipped) {
        this.shipped = shipped;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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
