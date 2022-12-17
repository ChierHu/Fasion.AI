package ai.fasion.fabs.diana.domain.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 点数sku PO
 */
public class PointSkuPO {

    @ApiModelProperty(value = "商品Id")
    private String id;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "金额")
    private Integer price;

    @ApiModelProperty(value = "点数")
    private Integer ptValuel;

    @ApiModelProperty(value = "状态：1--启动  2--禁用 ")
    private Integer status;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPtValuel() {
        return ptValuel;
    }

    public void setPtValuel(Integer ptValuel) {
        this.ptValuel = ptValuel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    @Override
    public String toString() {
        return "PointSkuPO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", price=" + price +
                ", ptValuel=" + ptValuel +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
