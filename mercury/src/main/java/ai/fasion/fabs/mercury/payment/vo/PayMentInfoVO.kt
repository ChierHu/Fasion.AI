package ai.fasion.fabs.mercury.payment.vo

/**
 * 套餐内容
 */
data class PayMentInfoVO(

    /**
     * 订单id
     */
    var orderId: String,
    /**
     * 二维码Url
     */
    var qrCodeUrl: String
)