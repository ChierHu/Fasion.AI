package ai.fasion.fabs.mercury.wechat.pojo

/**
 * Function: 订单信息(下订单时)
 *
 * @author miluo
 *Date: 2021/8/18 17:19
 * @since JDK 1.8
 */
data class OrderInfo(
    //应用ID
    var appid: String,
    //直连商户号
    var mchid: String,
    //商品描述
    var description: String,
    //商户订单号
    var outTradeNo: String,
    //回调地址
    var notifyUrl: String,
    //金额
    var amount: AmountInfo,
    //附加信息
    var attach: String? = null
)

/**
 * (orderInfo 的子类)
 * 订单金额
 */
data class AmountInfo(var total: Int, var currency: String = "CNY")

/**
 * 优惠功能
 */
data class DetailInfo(var costPrice: Int, var invoiceId: String)


