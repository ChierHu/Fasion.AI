package ai.fasion.fabs.diana.controller;

import ai.fasion.fabs.diana.common.PageRequestInfo;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.pojo.ResponseEmptyEntity;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.service.AssetsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api("素材管理")
@RestController
@RequestMapping("/assets")
public class AssetsController {

    private final AssetsService assetsService;

    private final PageRequestInfo pageRequestInfo;

    public AssetsController(AssetsService assetsService, PageRequestInfo pageRequestInfo) {
        this.assetsService = assetsService;
        this.pageRequestInfo = pageRequestInfo;
    }

    @ApiOperation(value = "素材查询")
    @GetMapping
    public ResponseEntity<Object> getAssets(@RequestParam(value = "type", required = false) String type, @RequestParam(value = "page", required = false) Integer page,
                                            @RequestParam(value = "scale", required = false, defaultValue = "75") Integer scale, String uid) {
        //如果page传为null需要设置初始值
        PageRequest pageRequest = pageRequestInfo.pageRequest(page);
        //素材查询
        AllInfoVO allInfoVO = assetsService.findAll(type, pageRequest, scale, uid);
        return new ResponseEntity<>(allInfoVO, HttpStatus.OK);
    }

    @ApiOperation("删除资源文件")
    @DeleteMapping("/{assetsId}")
    public ResponseEntity<Object> deleteByAssetId(@PathVariable("assetsId") String assetsId) {
        int result = assetsService.delectByAssetId(assetsId);
        if (result == 0) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }


}
