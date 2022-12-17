package ai.fasion.fabs.mercury.point.vo

/**
 * sku表
 */
data class SkuVO (
    /**
     * 编号
     */
    var id: String? =null,

    /**
     * sku
     */
    var sku:String? =null,

    /**
     * 名称
     */
    var name: String? =null,

    /**
     * 点数描述
     */
    var slogan: String? =null,

    /**
     * 价格(发布会不可修改)
     */
    var price: Int? =null,

    /**
     * 点数
     */
    var points: Int? =null,

    /**
     * 有效期 以天为单位
     */
    var expirationPeriod: Int? =null
)