package ai.fasion.fabs.diana.service;

import ai.fasion.fabs.diana.domain.po.PurchasePO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;

import javax.servlet.http.HttpServletRequest;

public interface PurchaseService {

    AllInfoVO purchaseList(PageRequest pageRequest);

    AllInfoVO purchaseListByType(String type, PageRequest pageRequest);

    PurchasePO refund(String purchaseId, HttpServletRequest httpRequest) throws URISyntaxException, IOException;

    PurchaseInfo purchase(String uid, HttpServletRequest httpRequest, String skuId, int amount) throws Exception;
}
