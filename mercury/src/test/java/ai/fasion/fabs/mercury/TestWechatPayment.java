package ai.fasion.fabs.mercury;

import ai.fasion.fabs.mercury.payment.PaymentMapper;
import ai.fasion.fabs.mercury.payment.PaymentService;
import ai.fasion.fabs.mercury.payment.po.PaymentPO;
import ai.fasion.fabs.mercury.wechat.TransactionService;
import ai.fasion.fabs.mercury.wechat.pojo.OrderResult;
import ai.fasion.fabs.mercury.wechat.pojo.RefundResult;
import ai.fasion.fabs.vesta.enums.PaymentEnum;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/16 17:29
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestWechatPayment {


    @Autowired
    public TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentService paymentService;

    /**
     * 获取平台证书
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void getPlatformCert() throws URISyntaxException, IOException {
        transactionService.getPlatformCert();
    }

    /**
     * 创建订单
     *
     * @throws Exception
     */
    @Test
    public void createOrder() throws Exception {
        String order = transactionService.createOrder("t-123456789", 1, "http://www.baicu.com", "支付", "附加信息");
        System.out.println(order);
    }

    /**
     * 需要先通过商户订单号查询到微信的订单号
     */
    @Test
    public void getOutTradeNo() throws URISyntaxException, IOException {
        OrderResult outTradeNo = transactionService.getOutTradeNo("pur-418971641536513");
        System.out.println(outTradeNo.toString());
    }

    /**
     * 订单号查询
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void getOrder() throws URISyntaxException, IOException {
        OrderResult outTradeNo = transactionService.getOrder("4200001225202108206860884780");
        System.out.println(outTradeNo.toString());
    }

    /**
     * 关闭订单
     */
    @Test
    public void closeOrder() throws IOException {
        transactionService.closeOrder("4200001137202108206193525100");
    }

    /**
     * 申请退款
     */
    @Test
    public void applyRefunds() throws IOException {
        RefundResult refundResult = transactionService.applyRefunds("4200001143202109295822326003", "pur-418971641536513", "想要退钱了", 1, 1);
        System.out.println(refundResult);
    }

    /**
     * 查询退款信息
     */
    @Test
    public void scoutRefunds() throws URISyntaxException, IOException {
        transactionService.scoutRefunds("t-123456");
    }

    /**
     * 添加Payment
     */
    @Test
    public void addPayment() {
        Map<String, Object> map = new HashMap<>();
        map.put("platform", "在");
        map.put("ip", "110.110.110.8");
        map.put("os", "在吗");
        map.put("user_snapshot", "哈哈哈");
        map.put("point_expired_at", "");
        //把时间添加进map
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("point_expired_at", sdf.format(new Date()));
        //map转json
        String metaJson = null;
        try {
            //map转换成Json
            metaJson = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        PaymentPO paymentPO = new PaymentPO("12346", "10470", PaymentEnum.Type.Refund.getLabel(), 100, 1000, PaymentEnum.Status.Succeed.getLabel(),
                PaymentEnum.Channel.WeChat.getLabel(), "666666", "777777", metaJson, null, null, null, null, null);
        paymentMapper.addPayment(paymentPO);
    }


    /**
     * 下订单(支付)
     */
    @Test
    public void createOrderWechat() throws Exception {
        String uid = AppThreadLocalHolder.getUserId();
        //HttpServletRequest httpRequest = new MockHttpServletRequest();
        String userAge = "user-age";
//        paymentService.getQrCodeUrl("10567", "113", PaymentEnum.Channel.WeChat, userAge);
    }


    /**
     * 查询订单状况
     */
    /*@Test
    public void scoutOrderWechat() throws Exception {
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        PurchasePO purchase = paymentService.findPurchase("pur-406998351634433", "10567", httpRequest);
        System.out.println(purchase.toString());

    }*/

}
