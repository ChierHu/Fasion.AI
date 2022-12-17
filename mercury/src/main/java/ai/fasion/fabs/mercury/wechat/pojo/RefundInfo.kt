package ai.fasion.fabs.mercury.wechat.pojo

/**
 * Function: 退款信息z
 *
 * @author miluo
 *Date: 2021/8/20 13:48
 * @since JDK 1.8
 */
data class RefundInfo(
    //微信支付订单号
    var transactionId: String,
    //商户订单号
    var outRefundNo: String,
    //退款原因
    var reason: String,
    //金额信息
    var amount: RefundsAmount
)

data class RefundsAmount(var refund: Int, var total: Int, var currency: String = "CNY")