package ai.fasion.fabs.diana.domain.vo;

import java.util.Date;

public class SkuVO {

    /**
     * sku主id
     */
    private String id;

    /**
     * sku
     */
    private String sku;

    /**
     * 名称
     */
    private String name;

    /**
     * 价格
     */
    private Integer price;

    /**
     * sku类型
     */
    private String type;

    /**
     * 点数描述
     */
    private String slogan;

    /**
     * 状态： enable--上架  disable--下架
     */
    private String status;

    /**
     * 点数
     */
    private Integer points;

    /**
     * 有效期
     */
    private Integer expirationPeriod;

    /**
     *创建时间
     */
    private Date createdAt;
}
