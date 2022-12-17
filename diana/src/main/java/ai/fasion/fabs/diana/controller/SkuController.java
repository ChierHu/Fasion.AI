package ai.fasion.fabs.diana.controller;

import ai.fasion.fabs.diana.domain.po.SkuPO;
import ai.fasion.fabs.diana.domain.vo.SkuInfoVO;
import ai.fasion.fabs.diana.service.SkuService;
import ai.fasion.fabs.vesta.enums.SkuEnum;
import ai.fasion.fabs.vesta.expansion.FailException;
import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "后台-充值套餐")
@RestController
@RequestMapping("/points/packs")
public class SkuController {

    private final SkuService skuService;

    public SkuController(SkuService skuService) {
        this.skuService = skuService;
    }

    @ApiOperation("查询充值套餐")
    @GetMapping
    public ResponseEntity<Object> skuInfoList(@RequestParam(value = "type",required = false) String type) {
        if (StringUtil.isNotEmpty(type) && !SkuEnum.Type.PointGift.getLabel().equals(type)){
            throw new FailException("此类型不支持查询");
        }
        List<SkuInfoVO> list = skuService.skuInfoList(type);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiModelProperty("添加充值套餐")
    @PostMapping
    public ResponseEntity<Object> addSku(@RequestBody SkuInfoVO skuInfoVO) {
        //slogan不能为空，不然调用微信支付接口的时候会报错
        if (StringUtil.isEmpty(skuInfoVO.getSlogan())){
            skuInfoVO.setSlogan("此处应有标语");
        }
        try {
            skuService.addSku(skuInfoVO);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("添加成功", HttpStatus.OK);
    }

    @ApiModelProperty("修改充值套餐")
    @PatchMapping(value = {"/{sku}"})
    public ResponseEntity<Object> updateSku(@PathVariable(value = "sku") String sku, @RequestBody SkuInfoVO skuInfoVO) {
        //slogan不能为空，不然调用微信支付接口的时候会报错
        if (StringUtil.isEmpty(skuInfoVO.getSlogan())){
            skuInfoVO.setSlogan("此处应有标语");
        }
        //如果单单只修改status，不需要进行快照修改，只在原来的数据修改status。修改其它则进行快照修改。
        try {
            skuService.updateSku(skuInfoVO, sku);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("修改成功", HttpStatus.OK);
    }

}
