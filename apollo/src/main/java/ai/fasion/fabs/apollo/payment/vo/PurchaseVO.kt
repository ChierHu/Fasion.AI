package ai.fasion.fabs.mercury.payment.vo

data class PurchaseVO(
    /**
     * 套餐名
     */
    var name: String? = null,

    /**
     * 订单id
     */
    var id: String? = null,

    /**
     * 创建时间
     */
    var createdAt: Any? = null,

    /**
     * 点数
     */
    var point: Int? = null,

    /**
     * 金额
     */
    var cash: Int? = null,

    /**
     * 订单状态(pending、succeeded、 cancelled)
     */
    var status: String? = null,

    /**
     * 渠道(wechat,alipay)
     */
    var channel: String? = null,

    /**
     * 数量
     */
    var amount: Int? = null
)