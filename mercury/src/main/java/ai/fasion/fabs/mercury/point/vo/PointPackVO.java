package ai.fasion.fabs.mercury.point.vo;

import java.util.Date;

/**
 * sku表
 */
public class PointPackVO {

    /**
     * 编号
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 点数描述
     */
    private String slogan;

    /**
     * 价格(发布会不可修改)
     */
    private Integer price;

    /**
     * 点数
     */
    private Integer points;

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

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "PointPackVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", slogan='" + slogan + '\'' +
                ", price=" + price +
                ", points=" + points +
                '}';
    }
}
