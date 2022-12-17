package ai.fasion.fabs.diana.service.impl;

import ai.fasion.fabs.diana.common.ReturnFormat;
import ai.fasion.fabs.diana.domain.po.PaymentPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.*;
import ai.fasion.fabs.diana.mapper.PaymentMapper;
import ai.fasion.fabs.diana.service.PaymentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;
    private final ReturnFormat returnFormat;

    public PaymentServiceImpl(PaymentMapper paymentMapper, ReturnFormat returnFormat) {
        this.paymentMapper = paymentMapper;
        this.returnFormat = returnFormat;
    }

    @Override
    public AllInfoVO paymentList(String uid, String type, PageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        //uid和type进行筛选查询流水list
        List<PaymentVO> list = paymentMapper.paymentList(uid, type);
        //所有的返回形式，数据统一保存到AllInfoVO
        AllInfoVO allInfoVO = returnFormat.format(list);
        return allInfoVO;
    }

    @Override
    public PaymentPO findById(String paymentId) {
        PaymentPO paymentPO = paymentMapper.findById(paymentId);
        return paymentPO;
    }
}
