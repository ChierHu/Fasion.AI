package ai.fasion.fabs.minerva.domain

import ai.fasion.fabs.vesta.NoArg

/**
 * Function: 存在于金山云对象存储中的配置信息
 *
 * @author miluo
 *Date: 2021/9/15 11:23
 * @since JDK 1.8
 */
@NoArg
data class ConfigInfo(
    //模块名称
    val minerva: MinervaInfo

    )

data class MinervaInfo(
    //sku信息
    var sku: MutableMap<String, String>?
)