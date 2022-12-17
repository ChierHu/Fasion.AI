package ai.fasion.fabs.mercury;

import ai.fasion.fabs.mercury.payment.PaymentMapper;
import ai.fasion.fabs.mercury.payment.PaymentServiceImpl;

import ai.fasion.fabs.mercury.payment.PurchaseMapper;
import ai.fasion.fabs.mercury.payment.PurchasePackDoubleList;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.payment.po.PurchasePackInfoVO;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.vesta.enums.SkuEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPayment {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PurchaseMapper purchaseMapper;


    @Test
    public void create() throws IOException, URISyntaxException {
        //查询订单，找到订单当前状态
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        PurchaseInfo order = paymentService.createOrder("100091", "XvvN58evY5", "wechat", 1, httpRequest.getHeader("user-agent"));
        System.out.println(order.toString());
    }

    @Test
    public void payment() throws IOException, URISyntaxException {
        //查询订单，找到订单当前状态
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        PurchasePO purchase = paymentService.findPurchase("pur-411939278487553", "10470", httpRequest.getHeader("user-agent"));
        System.out.println(purchase.toString());
    }

    @Test
    public void refundMoney() throws URISyntaxException, IOException {
        //后台退款功能
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        String userAgent = httpRequest.getHeader("user-agent");
        paymentService.refund("pur-412268248215553", userAgent);
    }

    @Test
    public void test001() {
        PurchasePO ret = paymentMapper.getPurchaseById("1", "10160");
        System.out.println(ret);
    }

    @Test
    public void calculateLongestWallet() {
        PurchasePackDoubleList purchasePackDoubleList = new PurchasePackDoubleList();

        //得到赠送的点数记录
        List<PurchasePackInfoVO> purchasePackInfoVOList = purchaseMapper.listUserPurchasePointPack("10160", SkuEnum.Type.PointGift.getLabel());
        //如果赠送的点数记录是空，那么需要对有序的用户钱包集合做初始化
        if (purchasePackInfoVOList != null) {
            purchasePackInfoVOList.forEach(item -> {
                purchasePackDoubleList.addLast(item);
            });
        }

        purchasePackInfoVOList = purchaseMapper.listUserPurchasePointPack("10160", SkuEnum.Type.PointPack.getLabel());
        if (purchasePackInfoVOList != null) {
            purchasePackInfoVOList.forEach(item -> {
                purchasePackDoubleList.addLast(item);
            });
        }
        System.out.println("执行结果如下：");
        System.out.println(purchasePackDoubleList.toString());

    }

    /**
     * 计算judgeCost实际剩余点数
     */
    @Test
    public void calculatePurchaseCost() {
        paymentService.judgeCost("10597", "xDKddRVeee", "pur-419286012174336");
    }

    /**
     * 测试双向链表是否可用
     */
    @Test
    public void testDoubleList() {
        PurchasePackDoubleList userLastWalletInfo = paymentService.getUserLastWalletInfo("10597");
        System.out.println(userLastWalletInfo.toString());
        System.out.println(userLastWalletInfo.getLength());
    }
}