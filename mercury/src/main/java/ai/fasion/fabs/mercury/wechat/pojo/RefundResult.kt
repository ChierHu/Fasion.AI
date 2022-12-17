package ai.fasion.fabs.mercury.wechat.pojo

import java.util.*

/**
 * 退款
 */
data class RefundResult(
    var amount: Amount,
    var refundId: String,
    var outRefundNo: String,
    var transactionId: String,
    var outTradeNo: String,
    var channel: String,
    var userReceivedAccount: String,
    var successTime: Date?,
    var createTime: Date,
    var status: String,
    var fundsAccount: String,
    var promotionDetail: List<PromotionDetailRefundVO>
)

data class Amount(
    var total: Int,
    var refund: Int,
    var from: List<FromRefundVO>,
    var payerTotal: Int,
    var payerRefund: Int,
    var settlementRefund: Int,
    var settlementTotal: Int,
    var discountRefund: Int,
    var currency: String
)

data class FromRefundVO(
    var account: String,
    var amount: Int = 0
)

data class PromotionDetailRefundVO(
    var promotionId: String,
    var scope: String,
    var type: String,
    var amount: Int,
    var refundAmount: Int,
    var goodsDetail: GoodsDetailRefundVO
)

data class GoodsDetailRefundVO(
    var merchantGoodsId: String,
    var wechatpayGoodsId: String,
    var goodsName: String,
    var unitPrice: Long,
    var refundAmount: Long,
    var refundQuantity: Long
)
