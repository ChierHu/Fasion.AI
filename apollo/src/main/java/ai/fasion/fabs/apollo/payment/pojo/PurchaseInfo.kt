package ai.fasion.fabs.mercury.payment.pojo

import java.util.*

/**
 * Function:
 *
 * @author miluo
 *Date: 2021/9/15 17:05
 * @since JDK 1.8
 */
data class PurchaseInfo(
    /**
     * 订单id
     */
    var id: String,

    /**
     * 用户id
     */
    var uid: String,

    /**
     * 资金表id
     */
    var payment: PaymentInfo,

    /**
     * sku表id
     */
    var skuId: String? = null,

    /**
     * 数量
     */
    var amount: Int,

    /**
     * 完成数量
     */
    var shipped: Int? = 0,

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


data class PaymentInfo(
    /**
     * 流水号（交易号）
     */
    var id: String,

    /**
     * 类型（充值、原路退款、转账退款）
     */
    var type: String,

    /**
     * 点数
     */
    var point: Int,

    /**
     * 渠道(WeChat、Alipay)
     */
    var channel: ChannelInfo,

    /**
     * 交易状态
     */
    var status: String
)

data class ChannelInfo(
    /**
     * 平台
     */
    val platform: String? =null,
    /**
     * 二维码Url
     */
    var qrCodeUrl: String? =null
)