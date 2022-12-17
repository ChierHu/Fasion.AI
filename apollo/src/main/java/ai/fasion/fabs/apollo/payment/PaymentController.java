package ai.fasion.fabs.apollo.payment;

import ai.fasion.fabs.apollo.common.PageRequestInfo;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.mercury.payment.vo.AllPaymentInfoVO;
import ai.fasion.fabs.mercury.payment.vo.PayMentInfoVO;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/18 14:32
 * @since JDK 1.8
 */
@RestController
@Api(tags = "充值接口")
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    /**
     * 十秒钟锁超时时间
     */
    private static final int TIMEOUT = 10 * 1000;

    private final PaymentService paymentService;
    private final PageRequestInfo pageRequestInfo;

    public PaymentController(PaymentService paymentService, PageRequestInfo pageRequestInfo) {
        this.paymentService = paymentService;
        this.pageRequestInfo = pageRequestInfo;
    }

    @ApiOperation("获取当前用户充值记录")
    @GetMapping("/payments")
    public ResponseEntity<Object> getRechargeRecord(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "type") String type) {
        String uid = AppThreadLocalHolder.getUserId();
        AllPaymentInfoVO responseEntity =  paymentService.findAllByUid(page, uid, type);
        return new ResponseEntity<>(responseEntity, HttpStatus.OK);
    }

    @ApiOperation("根据套餐id获取支付的二维码")
    @PostMapping("/purchases")
    public ResponseEntity<Object> getQrCodeUrl(HttpServletRequest httpRequest, @RequestParam(value = "id") @NotNull String id, @RequestParam(value = "channel") @NotNull String channel) throws Exception {
        String uid = AppThreadLocalHolder.getUserId();
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(channel)) {
            return new ResponseEntity<>("请传参完整参数并且参数带有值", HttpStatus.BAD_REQUEST);
        }
        PurchaseInfo purchaseInfo  = paymentService.getQrCodeUrl(uid, id, channel, httpRequest);
        return new ResponseEntity<>(purchaseInfo, HttpStatus.OK);
    }

    @ApiOperation("根据订单id返回订单数据")
    @GetMapping("/purchases/{id}")
    public PurchasePO listPurchase(HttpServletRequest httpRequest, @PathVariable(value = "id", required = false) String id) throws IOException, URISyntaxException {
        //获取用户id
        String uid = AppThreadLocalHolder.getUserId();
        PurchasePO purchasePO = paymentService.findPurchase(id, uid, httpRequest);
        return purchasePO;
    }

    @ApiOperation("type类型下返回用户下所有订单list")
    @GetMapping("/purchases")
    public AllPaymentInfoVO listPurchase(@RequestParam(value = "type", required = false) String type, @RequestParam(value = "page", required = false) Integer page) {
        //获取用户id
        String uid = AppThreadLocalHolder.getUserId();
        AllPaymentInfoVO allPaymentInfoVO = paymentService.purchaseList(type, uid, page);
        return allPaymentInfoVO;
    }

}