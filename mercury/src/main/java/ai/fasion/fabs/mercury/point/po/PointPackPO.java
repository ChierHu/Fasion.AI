package ai.fasion.fabs.mercury.point.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * sku表
 */
public class PointPackPO {

    /**
     * 编号
     */
    private String id;

    /**
     * sku
     */
    private String sku;

    /**
     * 版本号
     */
    private Integer revision;

    /**
     * 名称
     */
    private String name;

    /**
     * 价格(发布会不可修改)
     */
    private Integer price;

    /**
     * sku类型(point_pack、批量换脸……)
     */
    private String type;

    /**
     * 点数描述
     */
    private String slogan;

    /**
     * 状态（上下架）
     */
    private String status;

    /**
     * 目前存放points(integer)点数信息
     */
    private String props;

    /**
     * 创建时间
     */
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PointPackPO{" +
                "id='" + id + '\'' +
                ", sku='" + sku + '\'' +
                ", revision=" + revision +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", slogan='" + slogan + '\'' +
                ", status='" + status + '\'' +
                ", props='" + props + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
