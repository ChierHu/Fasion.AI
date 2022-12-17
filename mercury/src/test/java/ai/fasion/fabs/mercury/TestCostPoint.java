package ai.fasion.fabs.mercury;

import ai.fasion.fabs.mercury.payment.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Function: 测试计费系统可用性
 *
 * @author miluo
 * Date: 2021/9/22 13:43
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCostPoint {
    @Autowired
    private PaymentService paymentService;


    /**
     * 执行计费
     */
    @Test
    public void calculatePoint() {
//        paymentService.judgeCost("10009", "123", "2", 45);
    }


    /**
     * 执行退点
     */
    @Test
    public void calculateRefund() {
        paymentService.refundPoint("10009", "pur-416923747520512", 100);
    }
}
