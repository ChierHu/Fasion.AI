package ai.fasion.fabs.diana.domain.vo

data class SkuInfoVO(
    /**
     * 编号
     */
    var id: String? = null,

    /**
     * sku
     */
    var sku: String? = null,

    /**
     * 名称
     */
    var name: String? = null,

    /**
     * 点数描述
     */
    var slogan: String? = null,

    /**
     * 价格(发布会不可修改)
     */
    var price: Int? = null,

    /**
     * 状态 启用/禁用
     */
    var status: String? = null,

    var props: PropsInfo? = null
)

data class PropsInfo(
    /**
     * points(integer)点数信息
     */
    var points: Int? = null,

    /**
     * 点数有效期(integer)以天数为单位
     */
    var expiration_period: Int? = null
)