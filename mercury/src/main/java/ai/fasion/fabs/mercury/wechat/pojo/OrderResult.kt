package ai.fasion.fabs.mercury.wechat.pojo

import java.util.*

/**
 * 订单
 */
data class OrderResult(
    var amount: AmountResult,
    var appid: String,
    var attach: String?,
    var bankType: String?,
    var mchid: String,
    var outTradeNo: String,
    var payer: PayerResult?,
    var promotionDetail: List<Objects>,
    var successTime: Date?,
    var tradeState: String,
    var tradeStateDesc: String,
    var tradeType: String?,
    var transactionId: String?,
    var sceneInfo: SceneInfo?
)

data class AmountResult(
    var currency: String?,
    var payerCurrency: String,
    var payerTotal: Int?,
    var total: Int
)

data class PayerResult(
    var openid: String
)

data class SceneInfo(
    var deviceId: String?
)


