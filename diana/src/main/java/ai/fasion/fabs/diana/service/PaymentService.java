package ai.fasion.fabs.diana.service;

import ai.fasion.fabs.diana.domain.po.PaymentPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;

/**
 * 支付资金servie
 */
public interface PaymentService {

    /**
     * 通过type查询充值订单/退款订单/点数消费记录
     * @param uid
     * @param type
     * @return
     */
    AllInfoVO paymentList(String uid, String type, PageRequest pageRequest);

    PaymentPO findById (String paymentId);
}
