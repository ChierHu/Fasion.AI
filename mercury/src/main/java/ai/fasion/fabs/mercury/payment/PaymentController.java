package ai.fasion.fabs.mercury.payment;

import ai.fasion.fabs.mercury.component.RedisLock;
import ai.fasion.fabs.mercury.domain.PageRequest;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseMeta;
import ai.fasion.fabs.mercury.payment.vo.AllPaymentInfoVO;
import ai.fasion.fabs.mercury.payment.vo.PayMentInfoVO;
import ai.fasion.fabs.mercury.payment.vo.PurchaseVO;
import ai.fasion.fabs.mercury.point.po.SkuPO;
import ai.fasion.fabs.vesta.enums.PaymentEnum;
import ai.fasion.fabs.vesta.enums.SkuEnum;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.expansion.NotFoundException;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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
    private final RedisLock redisLock;

    public PaymentController(PaymentService paymentService, RedisLock redisLock) {
        this.paymentService = paymentService;
        this.redisLock = redisLock;
    }


    @ApiOperation("获取当前用户充值记录")
    @GetMapping("/payments")
    public AllPaymentInfoVO getRechargeRecord(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "type") String type, @RequestParam(value = "uid", required = true) String uid) {
        PageRequest pageRequest;
        if (null == page) {
            log.info("page is null ，use default constructor");
            pageRequest = new PageRequest();
        } else {
            log.info("page is not null ，use two parameter constructor");
            pageRequest = new PageRequest(page, 10);
        }
        AllPaymentInfoVO allPaymentInfoVO = paymentService.findAllByUid(pageRequest, uid, type);
        return allPaymentInfoVO;
    }

    @ApiOperation("根据套餐id下单（获取二维码、手工充值）")
    @PostMapping("/purchases")
    public PurchaseInfo purchase(@RequestParam(value = "userAgent", required = false) String userAgent, @RequestParam(value = "skuId") @NotNull String skuId, @RequestParam(value = "channel", required = false) String channel,
                                 @RequestParam(value = "amount", required = false) Integer amount, @RequestParam(value = "uid", required = false) String uid) throws Exception {
        if (StringUtils.isEmpty(skuId)) {
            throw new FailException("请传参完整参数并且参数带有值");
        }
        if (null == amount) {
            amount = 0;
        }
        return paymentService.createOrder(uid, skuId, channel, amount, userAgent);
    }

    @ApiOperation("根据订单id返回订单数据")
    @GetMapping("/purchases/{id}")
    public ResponseEntity<Object> getPurchase(@RequestParam(value = "userAgent") String userAgent, @PathVariable(value = "id", required = false) String id,
                                              @RequestParam(value = "uid") String uid) {
        PurchasePO purchase = null;
        // 如果订单id不为null
        if (id != null) {
            //上锁
            long time = System.currentTimeMillis() + TIMEOUT;
            redisLock.lock(id, String.valueOf(time), 5 * 60);
            try {
                purchase = paymentService.findPurchase(id, uid, userAgent);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            } finally {
                //解锁
                redisLock.unlock(id, String.valueOf(time));
            }
        }
        return new ResponseEntity<>(purchase, HttpStatus.OK);
    }

    @ApiOperation("type类型下返回用户下所有订单list")
    @GetMapping("/purchases")
    public ResponseEntity<Object> listPurchase(@RequestParam(value = "type", required = false) String type, @RequestParam(value = "page", required = false) Integer page,
                                               @RequestParam(value = "uid") String uid) {
        PageRequest pageRequest;
        if (null == page) {
            log.info("page is null ，use default constructor");
            pageRequest = new PageRequest();
        } else {
            log.info("page is not null ，use two parameter constructor");
            pageRequest = new PageRequest(page, 10);
        }
        AllPaymentInfoVO list = null;
        if (null == type || StringUtil.isEmpty(type)) {
            //查询所有订单类型下的数据
            list = paymentService.purchaseList(uid, pageRequest);
        } else {
            //查询按订单类型查询数据
            list = paymentService.purchaseListByType(type, uid, pageRequest);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation("原路退款")
    @PutMapping("/purchases/{purchaseId}")
    public PurchasePO refund(@PathVariable("purchaseId") @NotNull @NotEmpty String purchaseId, @RequestParam(value = "userAgent") String userAgent) throws URISyntaxException, IOException {
        return paymentService.refund(purchaseId, userAgent);
    }


    /**
     * 点数预估
     *
     * @param uid
     * @param taskId
     * @return
     */
    @PostMapping("/purchases/evaluation")
    public ResponseEntity<Object> evaluation(@RequestParam(value = "uid") String uid, @RequestParam(value = "taskId") String taskId, @RequestParam(value = "skuId") String skuId, @RequestParam(value = "amount") int amount) {//上锁
        PurchaseMeta purchaseInfo = paymentService.evaluation(uid, taskId, skuId, amount);
        return new ResponseEntity<>(purchaseInfo, HttpStatus.OK);
    }

    /**
     * 任务计费
     *
     * @param uid
     * @param taskId
     * @param purchaseId
     * @return
     */
    @PostMapping("/purchases/cost")
    public ResponseEntity<Object> judgeCost(@RequestParam(value = "uid") String uid, @RequestParam(value = "taskId") String taskId, @RequestParam(value = "purchaseId") String purchaseId) {//上锁
        long time = System.currentTimeMillis() + TIMEOUT;
        redisLock.lock(uid, String.valueOf(time), 5 * 60);
        try {
            //(String uid, String taskId, String purchaseId
            String purchaseInfo = paymentService.judgeCost(uid, taskId, purchaseId);
            return new ResponseEntity<>(purchaseInfo, HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.PAYMENT_REQUIRED);
        }finally {
            //解锁
            redisLock.unlock(uid, String.valueOf(time));
        }
    }

    @ApiOperation("退费")
    @PostMapping("purchases/refundPoint")
    public ResponseEntity<Object> refundPoint(@RequestParam(value = "uid") String uid, @RequestParam(value = "taskId") String taskId, @RequestParam(value = "pointAmount") Integer pointAmount) {
        //上锁
        long time = System.currentTimeMillis() + TIMEOUT;
        redisLock.lock(uid, String.valueOf(time), 5 * 60);
        try {
            paymentService.refundPoint(uid, taskId, pointAmount);
            return new ResponseEntity<>("退费成功", HttpStatus.OK);
        } finally {
            //解锁
            redisLock.unlock(uid, String.valueOf(time));
        }
    }

}