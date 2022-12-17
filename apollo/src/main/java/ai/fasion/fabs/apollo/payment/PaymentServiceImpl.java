package ai.fasion.fabs.apollo.payment;

import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.mercury.payment.vo.AllPaymentInfoVO;
import ai.fasion.fabs.mercury.payment.vo.PayMentInfoVO;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.utils.RestTemplateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;


/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/16 17:25
 * @since JDK 1.8
 */
@Service
public class PaymentServiceImpl implements PaymentService {


    @Value("${apollo.mercury.url}")
    private String mercuryUrl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 根据uid获取用户下所有支付信息
     *
     * @param page
     * @param uid
     * @return
     */
    @Override
    public AllPaymentInfoVO findAllByUid(Integer page, String uid, String type) {
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = mercuryUrl + "/payments?page=" + page + "&type=" + type + "&uid=" + uid;
        ResponseEntity<AllPaymentInfoVO> responseEntity = RestTemplateUtils.get(url, AllPaymentInfoVO.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        AllPaymentInfoVO allPaymentInfoVO = responseEntity.getBody();
        return allPaymentInfoVO;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public PurchaseInfo getQrCodeUrl(String uid, String id, String channel, HttpServletRequest httpRequest) throws IOException {
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userAgent = httpRequest.getHeader("user-agent");
        String url = mercuryUrl + "/purchases?uid=" + uid + "&channel=" + channel + "&userAgent=" + userAgent + "&skuId=" + id;
        ResponseEntity<String> responseEntity = RestTemplateUtils.post(url, String.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //json转换成对象
        PurchaseInfo purchaseInfo = objectMapper.readValue(responseEntity.getBody(), PurchaseInfo.class);
        return purchaseInfo;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public PurchasePO findPurchase(String id, String uid, HttpServletRequest httpRequest) {
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userAgent = httpRequest.getHeader("user-agent");
        String url = mercuryUrl + "/purchases/" + id + "?uid=" + uid + "&userAgent=" + userAgent;
        ResponseEntity<PurchasePO> responseEntity = RestTemplateUtils.get(url, PurchasePO.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        PurchasePO result = responseEntity.getBody();
        return result;
    }

    @Override
    public AllPaymentInfoVO purchaseList(String type, String uid, Integer page) {
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = mercuryUrl + "/purchases?type=" + type + "&page=" + page + "&uid=" + uid;
        ResponseEntity<AllPaymentInfoVO> responseEntity = RestTemplateUtils.get(url, AllPaymentInfoVO.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        AllPaymentInfoVO result = responseEntity.getBody();
        return result;
        }
    }
