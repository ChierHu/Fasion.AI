package ai.fasion.fabs.mercury.payment.vo

data class AllPointInfoVO (
    var total: Long? =null,
    var links: Link? = null,
    var data: Any? =null
)
data class Link(
    var next: String?,
    var last: String
)