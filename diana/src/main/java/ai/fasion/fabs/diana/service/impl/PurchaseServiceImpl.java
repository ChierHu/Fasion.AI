package ai.fasion.fabs.diana.service.impl;

import ai.fasion.fabs.diana.domain.po.PurchasePO;
import ai.fasion.fabs.diana.common.ReturnFormat;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.domain.vo.PurchaseVO;
import ai.fasion.fabs.diana.mapper.PaymentMapper;
import ai.fasion.fabs.diana.mapper.PurchaseMapper;
import ai.fasion.fabs.diana.service.PurchaseService;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.vesta.enums.PurchaseEnum;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.utils.RestTemplateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Value("${diana.mercury.url}")
    private String mercuryUrl;

    private final PurchaseMapper purchaseMapper;

    private final PaymentMapper paymentMapper;

    private final ReturnFormat returnFormat;

    private final ObjectMapper objectMapper;

    public PurchaseServiceImpl(PurchaseMapper purchaseMapper, PaymentMapper paymentMapper, ReturnFormat returnFormat, ObjectMapper objectMapper) {
        this.purchaseMapper = purchaseMapper;
        this.paymentMapper = paymentMapper;
        this.returnFormat = returnFormat;
        this.objectMapper = objectMapper;
    }


    /**
     * 查询订单list
     *
     * @param pageRequest
     * @param pageRequest
     * @return
     */
    @Override
    public AllInfoVO purchaseList(PageRequest pageRequest) {
        List<Object> list  = new ArrayList<>();
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<PurchaseVO> pointPackList = purchaseMapper.purchaseList(PurchaseEnum.Type.PointPack.getLabel());
        List<PurchaseVO> faceSwapList = purchaseMapper.purchaseTypeList(PurchaseEnum.Type.FaceSwap.getCode());
        if (!faceSwapList.isEmpty()) {
            faceSwapList.forEach(v -> {
                v.setType(PurchaseEnum.Type.FaceSwap.getLabel());
            });
        }
        List<PurchaseVO> mattingImageList = purchaseMapper.purchaseTypeList(PurchaseEnum.Type.MattingImage.getCode());
        if (!mattingImageList.isEmpty()) {
            mattingImageList.forEach(v -> {
                v.setType(PurchaseEnum.Type.MattingImage.getLabel());
            });
        }
        list.addAll(pointPackList);
        list.addAll(faceSwapList);
        list.addAll(mattingImageList);
        AllInfoVO allInfoVO = returnFormat.format(list);
        return allInfoVO;
    }

    /**
     * 根据订单类型(必须有值的情况下) 查询订单list
     *
     * @param type
     * @param pageRequest
     * @return
     */
    @Override
    public AllInfoVO purchaseListByType(String type, PageRequest pageRequest) {
        List<PurchaseVO> list = new ArrayList<>();
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        if (PurchaseEnum.Type.PointPack.getLabel().equals(type)) {
            list = purchaseMapper.purchaseList(PurchaseEnum.Type.PointPack.getLabel());
        }
        if (PurchaseEnum.Type.FaceSwap.getLabel().equals(type)) {
            list = purchaseMapper.purchaseTypeList(PurchaseEnum.Type.FaceSwap.getCode());
            if (!list.isEmpty()) {
                list.forEach(v -> {
                    v.setType(PurchaseEnum.Type.FaceSwap.getLabel());
                });
            }
        }
        if (PurchaseEnum.Type.MattingImage.getLabel().equals(type)) {
            list = purchaseMapper.purchaseTypeList(PurchaseEnum.Type.MattingImage.getCode());
            if (!list.isEmpty()) {
                list.forEach(v -> {
                    v.setType(PurchaseEnum.Type.MattingImage.getLabel());
                });
            }
        }
        //所有的返回形式，数据统一保存到AllInfoVO
        AllInfoVO allInfoVO = returnFormat.format(list);
        return allInfoVO;
    }

    @Override
    public PurchasePO refund(String purchaseId, HttpServletRequest httpRequest) throws URISyntaxException, IOException {
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userAgent = httpRequest.getHeader("user-agent");
        String url = mercuryUrl + "/purchases/" + purchaseId + "?userAgent=" + userAgent;
        ResponseEntity<PurchasePO> responseEntity = RestTemplateUtils.put(url, PurchasePO.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        PurchasePO purchasePO = responseEntity.getBody();
        return purchasePO;
    }


    public PurchaseInfo purchase(String uid, HttpServletRequest httpRequest, String skuId, int amount) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userAgent = httpRequest.getHeader("user-agent");
        String url = mercuryUrl + "/purchases?uid=" + uid + "&userAgent=" + userAgent + "&skuId=" + skuId + "&amount=" + amount;
        ResponseEntity<String> responseEntity = RestTemplateUtils.post(url, String.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //json转换成对象
        PurchaseInfo purchaseInfo = objectMapper.readValue(responseEntity.getBody(), PurchaseInfo.class);
        return purchaseInfo;


    }
}
