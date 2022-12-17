package ai.fasion.fabs.diana.domain.vo

data class SkuOtherInfoVO(
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

    var props: String? = null
)