package ai.fasion.fabs.mercury.payment;

import ai.fasion.fabs.mercury.domain.PageRequest;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseMeta;
import ai.fasion.fabs.mercury.payment.vo.AllPaymentInfoVO;
import ai.fasion.fabs.mercury.payment.vo.PayMentInfoVO;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.payment.vo.PurchaseVO;
import ai.fasion.fabs.mercury.point.po.SkuPO;
import ai.fasion.fabs.vesta.enums.PaymentEnum;
import ai.fasion.fabs.vesta.expansion.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/16 17:24
 * @since JDK 1.8
 */
public interface PaymentService {

    AllPaymentInfoVO findAllByUid(PageRequest pageRequest, String uid, String type);

    /**
     * 下订单
     *
     * @param uid
     * @param skuId
     * @param channel
     * @param amount
     * @param userAgent
     * @return
     */
    PurchaseInfo createOrder(String uid, String skuId, String channel, Integer amount, String userAgent) throws IOException;

    /**
     * 返回 CodeUrl
     *
     * @param uid
     * @param skuInfo
     * @param channel
     * @return
     * @throws Exception
     */
    PayMentInfoVO getQrCodeUrl(String uid, SkuPO skuInfo, PaymentEnum.Channel channel, String purchaseId, String paymentId, String metaJson) throws Exception;


    /**
     * 查询用户下的订单数据
     */
    AllPaymentInfoVO purchaseList(String uid, PageRequest pageRequest);


    /**
     * 查询用户的订单类型查询数据
     */
    AllPaymentInfoVO purchaseListByType(String type, String uid, PageRequest pageRequest);

    /**
     * 根据订单id返回用户订单数据
     *
     * @param id
     * @param uid
     * @return
     */
    PurchasePO findPurchase(String id, String uid, String userAgent) throws IOException, URISyntaxException;

    PurchasePO refund(String purchaseId, String userAgent) throws URISyntaxException, IOException;


    PurchasePO purchase(String uid, String userAgent, SkuPO skuInfo, Integer amount, String purchaseId, String paymentId, String metaJson);


    public String judgeCost(String uid, String taskId, String purchaseId);


    void refundPoint(String uid, String purchaseId, int pointAmount);

    public PurchaseMeta evaluation(String uid, String taskId, String skuId, int amount);
}
