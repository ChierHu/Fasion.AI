package ai.fasion.fabs.minerva

import ai.fasion.fabs.minerva.domain.FileInfo
import ai.fasion.fabs.vesta.utils.KsOssUtil
import com.ksyun.ks3.dto.CannedAccessControlList
import com.ksyun.ks3.dto.Ks3ObjectSummary
import org.springframework.stereotype.Service
import java.io.File

/**
 * Function: 对象存储service
 *
 * @author miluo
 *Date: 2021/7/1 16:08
 * @since JDK 1.8
 */
interface FilesService {

    fun upload(name: String, key: String, file: File): FileInfo

    fun getFileInfos(name: String, key: String): List<FileInfo>

}

@Service
class FilesServiceImpl : FilesService {
    override fun upload(name: String, key: String, file: File): FileInfo {

        //上传文件
        KsOssUtil.getInstance().putObjectSimple(name, key, file, CannedAccessControlList.Private)

        val objectExists = KsOssUtil.getInstance().objectExists(name, key)
        if (objectExists) {
            val shiftName = KsOssUtil.getInstance().shiftName(name, key)
            return FileInfo(key = key, link = shiftName)
        } else {
            throw RuntimeException("上传失败")
        }
    }

    override fun getFileInfos(name: String, key: String): List<FileInfo> {
        val listObjectsWithPrefix = KsOssUtil.getInstance().listObjectsWithPrefix(name, key)
        val objectSummaries: List<Ks3ObjectSummary> = listObjectsWithPrefix[0].objectSummaries

        if (objectSummaries.size > 100) {
            throw RuntimeException("查询的文件过大")
        }


        return listOf(FileInfo(key = key, link = ""))
    }

}
