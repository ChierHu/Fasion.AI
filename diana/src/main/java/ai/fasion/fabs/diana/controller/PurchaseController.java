package ai.fasion.fabs.diana.controller;


import ai.fasion.fabs.diana.common.PageRequestInfo;
import ai.fasion.fabs.diana.domain.po.PurchasePO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.service.PurchaseService;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@Api(tags = "订单管理")
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    private final PageRequestInfo pageRequestInfo;

    public PurchaseController(PurchaseService purchaseService, PageRequestInfo pageRequestInfo) {
        this.purchaseService = purchaseService;
        this.pageRequestInfo = pageRequestInfo;
    }

    @ApiOperation("订单列表")
    @GetMapping
    public ResponseEntity<Object> purchaseList(@RequestParam(value = "type", required = false) String type, @RequestParam(value = "page", required = false) Integer page) {
        AllInfoVO allPurchaseInfo = null;
        PageRequest pageRequest = pageRequestInfo.pageRequest(page);
        if (StringUtils.isEmpty(type)) {
            //如果传的订单类型为null查询所有的订单类型下的数据
            allPurchaseInfo = purchaseService.purchaseList(pageRequest);
        } else {
            allPurchaseInfo = purchaseService.purchaseListByType(type, pageRequest);
        }
        return new ResponseEntity<>(allPurchaseInfo, HttpStatus.OK);
    }


    @ApiOperation("原路退款")
    @PutMapping("/{purchaseId}")
    public ResponseEntity<Object> refund(@PathVariable("purchaseId") @NotNull @NotEmpty String purchaseId, HttpServletRequest httpRequest) throws URISyntaxException, IOException {
        PurchasePO purchasePO = purchaseService.refund(purchaseId, httpRequest);
        return new ResponseEntity<>(purchasePO, HttpStatus.OK);
    }

    @ApiOperation("手工充值赠送点数")
    @PostMapping("/purchase")
    public PurchaseInfo purchase(@RequestParam(value = "uid") String uid, HttpServletRequest httpRequest, @RequestParam(value = "skuId") String skuId,
                                 @RequestParam(value = "amount") int amount) throws Exception {
        return purchaseService.purchase(uid, httpRequest, skuId, amount);
    }
}
