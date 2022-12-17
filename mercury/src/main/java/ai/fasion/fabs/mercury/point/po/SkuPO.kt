package ai.fasion.fabs.mercury.point.po

import java.util.*

/**
 * sku表
 */
data class SkuPO @JvmOverloads constructor(
    /**
     * 编号
     */
    var id: String? =null,

    /**
     * sku
     */
    var sku: String? =null,

    /**
     * 版本号
     */
    var revision: Int? =null,

    /**
     * 名称
     */
    var name: String? =null,

    /**
     * 价格(发布会不可修改)
     */
    var price: Int? =null,

    /**
     * sku类型(point_pack、批量换脸……)
     */
    var type: String? = null,

    /**
     * 点数描述
     */
    var slogan: String? =null,

    /**
     * 状态（上下架）
     */
    var status: String? =null,

    /**
     * 目前存放points(integer)点数信息,点数有效期：expiration_period
     */
    var props: String? =null,

    /**
     * 点数
     */
    var points: Int? =null,

    /**
     * 有效期
     */
    var expirationPeriod: Int? =null,

    /**
     * 创建时间
     */
    var createdAt: Any? = null
)