package ai.fasion.fabs.mercury.payment.vo

import java.util.*

/**
 * 资金表
 */
data class PaymentVO (
    /**
     * 流水号（交易号）
     */
    var id: String,

    /**
     * 套餐名称
     */
    var name: String,

    /**
     * 类型（充值、原路退款、转账退款）
     */
    var type: String,

    /**
     * 金额
     */
    var cash: Int,

    /**
     * 点数
     */
    var point: Int,

    /**
     * 渠道(WeChat、Alipay)
     */
    var channel: String,

    /**
     * 交易状态
     */
    var status: String,

    /**
     * 创建时间
     */
    var createdAt: Date,

    /**
     * 修改时间
     */
    var updatedAt: Date,

    /**
     * 完成时间
     */
    var finishedAt: Date?=null
)