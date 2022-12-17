package ai.fasion.fabs.apollo.payment;

import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.mercury.payment.vo.AllPaymentInfoVO;
import ai.fasion.fabs.mercury.payment.vo.PayMentInfoVO;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/16 17:24
 * @since JDK 1.8
 */
public interface PaymentService {

    AllPaymentInfoVO findAllByUid(Integer page, String uid, String type);

    /**
     * 返回 CodeUrl
     *
     * @param uid
     * @param id
     * @param channel
     * @return
     * @throws Exception
     */
    PurchaseInfo getQrCodeUrl(String uid, String id, String channel, HttpServletRequest httpRequest) throws Exception;

    /**
     * 根据订单id返回用户订单数据
     *
     * @param id
     * @param uid
     * @return
     */
    PurchasePO findPurchase(String id, String uid, HttpServletRequest httpRequest) throws IOException, URISyntaxException;

    /**
     * 查询所有用户下的订单id
     */
    AllPaymentInfoVO purchaseList(String type, String uid, Integer page);


}
