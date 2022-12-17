package ai.fasion.fabs.mercury.payment.po

import java.util.*

/**
 * 订单表
 */
data class PurchasePO(
    /**
     * 订单id
     */
    var id: String? = null,

    /**
     * 用户id
     */
    var uid: String? = null,

    /**
     * 资金表id
     */
    var payments: Array<String>? = null,

    /**
     * sku表id
     */
    var skuId: String? = null,

    /**
     * 数量
     */
    var amount: Int? = null,

    /**
     * task表id 或 payment表id
     */
    var productId: String? = null,

    /**
     * 状态(pending、succeeded、 cancelled)
     */
    var status: String,

    /**
     * 创建时间
     */
    var createdAt: Date? = null,

    /**
     * 修改时间
     */
    var updatedAt: Date? = null,

    /**
     * 完成时间
     */
    var finishedAt: Date? = null

)