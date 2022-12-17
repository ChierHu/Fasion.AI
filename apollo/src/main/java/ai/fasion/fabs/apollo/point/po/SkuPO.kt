package ai.fasion.fabs.mercury.point.po

import java.util.*

/**
 * sku表
 */
data class SkuPO (
    /**
     * 编号
     */
    var id: String,

    /**
     * sku
     */
    var sku: String,

    /**
     * 版本号
     */
    var revision: Int,

    /**
     * 名称
     */
    var name: String,

    /**
     * 价格(发布会不可修改)
     */
    var price: Int,

    /**
     * sku类型(point_pack、批量换脸……)
     */
    var type: String? = null,

    /**
     * 点数描述
     */
    var slogan: String,

    /**
     * 状态（上下架）
     */
    var status: String,

    /**
     * 目前存放points(integer)点数信息,点数有效期：expiration_period
     */
    var props: String,

    /**
     * 创建时间
     */
    var createdAt: Any? = null
)