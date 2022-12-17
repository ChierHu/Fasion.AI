package ai.fasion.fabs.apollo.assets;

import ai.fasion.fabs.apollo.config.annotation.CurrentUserInfo;
import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.apollo.assets.pojo.Catalogue;
import ai.fasion.fabs.apollo.assets.pojo.Policy;
import ai.fasion.fabs.apollo.domain.ResponseEmptyEntity;
import ai.fasion.fabs.apollo.assets.pojo.Ticket;
import ai.fasion.fabs.apollo.assets.vo.AssetVO;
import ai.fasion.fabs.apollo.assets.vo.TicketVO;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Function:assets controller
 * 2500+1250=
 *
 * @author miluo
 * Date: 2021/5/26 15:50
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/assets")
@Api(tags = "素材凭证")
public class AssetsController {

    private static final Logger log = LoggerFactory.getLogger(AssetsController.class);

    @Autowired
    private KsOssConstant ksOssConstant;

    @Autowired
    private AssetsService assetService;

    /**
     * 获取素材凭证
     *
     * @param type
     * @return
     */
    @ApiOperation("获取素材凭证")
    @PostMapping("/ticket")
    public ResponseEntity<Object> ticket(@RequestParam String type, @CurrentUserInfo UserInfoDO userInfo) {
        log.info("get ticket : user_id->{} type->{}", userInfo.getId(), type);
        Asset.Type assetEnum = Asset.Type.assetTypeOf(type);
        TicketVO ticketInfo = assetService.generateTicketInfo(userInfo.getId(), assetEnum, new Policy());
        return new ResponseEntity<>(ticketInfo, HttpStatus.OK);
    }

    /**
     * 回收素材凭证
     *
     * @param ticket
     * @return
     */
    @ApiOperation("回收素材凭证")
    @PostMapping("/check")
    public ResponseEntity<Object> check(@RequestParam String ticket) {
        String ownerId = AppThreadLocalHolder.getUserId();
        log.info("check ticket : user_id->{} ticket->{}", ownerId, ticket);
        //得到ticket后需要先在redis中找到ticket在金山云的path
        Ticket ticketInfo = assetService.judgeTicket(ticket);
        //如果ticket不存在则返回404
        if (null == ticketInfo) {
            return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.NOT_FOUND);
        }
        assetService.chancelTicket(ticket);
        //判断ticket是否属于当前用户
        assert ownerId != null;
        if (!ownerId.equals(ticketInfo.getUid())) {
            return new ResponseEntity<>("ticket不属于当前用户", HttpStatus.FORBIDDEN);
        }
        //对文件进行重命名
         assetService.corrections(ticketInfo.getPath());
        //找到之后，需要给所有文件编号，编号之后生成一个List<String> ids
        List<Catalogue> distribution = assetService.distribution(ticketInfo, ticket);
        return new ResponseEntity<>(distribution, HttpStatus.OK);
    }

    @ApiOperation("获取素材列表（特定类型）")
    @GetMapping
    public ResponseEntity<Object> assets(@RequestParam(value = "type", required = false) String type, @RequestParam(value = "bundleId", required = false) String bundleId, @RequestParam(value = "scale", required = false, defaultValue = "25") Integer scale) {
        List<AssetVO> assetVOList = assetService.selectByType(type, AppThreadLocalHolder.getUserId(), bundleId);
        if (null == assetVOList) {
            return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.NOT_FOUND);
        }
        List<Map<String, Object>> list = new ArrayList<>();
        assetVOList.forEach(v -> {
            Map<String, Object> map = new HashMap<>(4);
            map.put("id", v.getAssetId());
            map.put("type", v.getType().getLabel());
            map.put("thumbnail", KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), v.getPath() + "@base@tag=imgScale&p=" + scale));
            map.put("link", KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), v.getPath()));
            list.add(map);
        });

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation("获取素材详情")
    @GetMapping("/{assetId}")
    public ResponseEntity<Object> assetsByAssetId(@PathVariable(value = "assetId") String assetId, @RequestParam(value = "scale", required = false, defaultValue = "25") Integer scale) {
        AssetVO assetVO = assetService.findByAssetId(assetId, AppThreadLocalHolder.getUserId());
        Map<String, Object> map = new HashMap<>(4);
        map.put("id", assetVO.getAssetId());
        map.put("type", Asset.Type.valueOf(assetVO.getType().toString()).getLabel());
        if (null == scale) {
            scale = 25;
        }
        map.put("thumbnail", KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), assetVO.getPath() + "@base@tag=imgScale&p=" + scale));
        map.put("link", KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), assetVO.getPath()));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @ApiOperation("删除素材 （特定类型）")
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Object> deleteByAssetId(@PathVariable("assetId") String assetId) {
        log.info("get into [deleteByAssetId]");
        int result = assetService.deleteByAssetId(assetId);
        if (result == 0) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }


    @ApiOperation("上传素材")
    @PostMapping
    public ResponseEntity<Object> uploadByAssetId(@RequestParam(value = "type") String type, @RequestParam(value = "ticketId") String ticketId,
                                                  @RequestParam("file") @NotNull(message = "未找到上传资源") MultipartFile file) throws IOException {
        log.info("get into [uploadByAssetId]");
        //1.判断ticket id是否存在 (judgeTicket)
        Ticket ticketInfo = assetService.judgeTicket(ticketId);
        //如果ticket不存在则返回404
        if (null == ticketInfo) {
            return new ResponseEntity<>("ticketId失效", HttpStatus.NOT_FOUND);
        }
        //2.存在后获取ticket id路径
        //3.判断传过来的type类型和第二步返回的ticker id的类型是否一致
        if (!ticketInfo.getType().equals(type)) {
            throw new FailException("error");
        }
        //4.上述都通过后，将文件转发到56300项目中
        Map<String, Object> map = assetService.uploadByAssetId(ticketId, file, ticketInfo);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}

