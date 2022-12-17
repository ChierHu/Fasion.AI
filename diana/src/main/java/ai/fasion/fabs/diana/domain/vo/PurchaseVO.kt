package ai.fasion.fabs.diana.domain.vo

import java.util.*

data class PurchaseVO(
    var id: String? = null,
    var createdAt: Any? = null,
    var type: String? = null,
    var amount: Int? = null,
    var shipped: Int? =null,
    var payments: Array<String>? = null,
    var status: String? =null
)