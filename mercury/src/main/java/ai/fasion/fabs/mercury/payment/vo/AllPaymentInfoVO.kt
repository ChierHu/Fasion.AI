package ai.fasion.fabs.mercury.payment.vo

data class AllPaymentInfoVO(
    var total: Int?=null,
    var links: LinkVO?=null,
    var data: Any?=null
)

data class LinkVO(
    var next: String?=null,
    var last: String?=null
)