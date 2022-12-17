package ai.fasion.fabs.minerva

import ai.fasion.fabs.minerva.domain.FileInfo
import ai.fasion.fabs.vesta.utils.MD5Utils
import ai.fasion.fabs.vesta.utils.MultipartFileToFile
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.HandlerMapping
import javax.servlet.http.HttpServletRequest


/**
 * Function: 对象存储上传查询接口
 *
 * @author miluo
 *Date: 2021/7/1 16:04
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/buckets")
class FilesController(
    private val filesService: FilesService
) {

    @PostMapping("/{name}/**")
    fun <T> postUpload(
        @PathVariable("name") name: String,
        @RequestParam("file") file: MultipartFile,
        request: HttpServletRequest
    ): FileInfo {
        val path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString()
        val key = AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path)

        val fileInfo = MultipartFileToFile.multipartFileToFile(file)

        val encryptSalt =
            MD5Utils.encryptSalt(System.currentTimeMillis().toString() + file.originalFilename, file.originalFilename)

        val keyName = key + "/" + encryptSalt + "." + fileInfo.extension

        return filesService.upload(name, keyName, fileInfo)
    }

    @PutMapping("/{name}/**")
    fun <T> putUpload(
        @PathVariable("name") name: String,
        @RequestParam("file") file: MultipartFile,
        request: HttpServletRequest
    ): FileInfo {
        val path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString()
        val key: String = AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path)
        if (key.lastIndexOf(".") == -1) {
            throw RuntimeException("未匹配到文件")
        }
        return filesService.upload(name, key, MultipartFileToFile.multipartFileToFile(file))
    }


    @GetMapping("/{name}/**")
    fun <T> getFile(
        @PathVariable("name") name: String,
        request: HttpServletRequest
    ): List<FileInfo> {
        val path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString()
        val key: String = AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path)

        return filesService.getFileInfos(name, key)
    }
}







