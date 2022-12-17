package ai.fasion.fabs.minerva

import ai.fasion.fabs.minerva.constant.KsOssConstant
import ai.fasion.fabs.minerva.domain.ConfigInfo
import ai.fasion.fabs.minerva.domain.MinervaInfo
import ai.fasion.fabs.vesta.utils.KsOssUtil
import ai.fasion.fabs.vesta.utils.RestTemplateUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.ksyun.ks3.AutoAbortInputStream
import com.ksyun.ks3.dto.GetObjectResult
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity

/**
 * Function:测试金山云oss中的配置文件
 *
 * @author miluo
 *Date: 2021/9/15 11:29
 * @since JDK 1.8
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestKs3Config(
    @Autowired
    val ksOssConstant: KsOssConstant,

    @Autowired
    val objectMapper: ObjectMapper
) {

    /**
     * 测试config.json文件的格式
     */
    @Test
    fun `check config`() {

        val sku = mutableMapOf(Pair("face-swap", "Qy2ek0nVPj"), Pair("matting-image", "YL3P7j8bQL"))
        val minerva = MinervaInfo(sku)
        val configInfo = ConfigInfo(minerva)
        val json: String = objectMapper.writeValueAsString(configInfo)
        println(json)
    }

    @Test
    fun `download config`() {
        val getObjectResult: GetObjectResult =
            KsOssUtil.getInstance().getObject(ksOssConstant.bucketName, "config/config.json")
        var objectContent: AutoAbortInputStream = getObjectResult.`object`.objectContent
        var text = objectContent.bufferedReader().use { it.readText() }
        println(text)
    }


    @Test
    fun `test net`() {
        val get: ResponseEntity<String> = RestTemplateUtils.get("http://www.baidu.com", String::class.java)




        println(get)
    }


    @Test
    fun `test basic`() {
        val a = "Kotlin"
        val b: String? = null
        println(a.length)
        println(b?.length)

        val l = b!!.length
    }

}