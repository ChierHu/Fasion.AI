package ai.fasion.fabs.mercury.payment.vo

data class PurchaseOtherVO (
    /**
     * 订单id
     */
    var id: String,

    /**
     * 创建时间
     */
    var createdAt: Any? = null,

    /**
     * sku类型(point_pack、批量换脸……) |
     */
    var type: String,

    /**
     * 数量
     */
    var amount: Int,

    /**
     * 消费(点)
     */
    var point: Int

)