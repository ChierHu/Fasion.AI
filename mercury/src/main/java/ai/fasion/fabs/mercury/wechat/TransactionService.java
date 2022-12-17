package ai.fasion.fabs.mercury.wechat;

import ai.fasion.fabs.mercury.wechat.pojo.*;
import ai.fasion.fabs.vesta.expansion.FailException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/20 11:54
 * @since JDK 1.8
 */
@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final String notifyUrl = "http://play.fasionai.com/wechat/payment/callback";
    private final ObjectMapper objectMapper;
    private final TransactionProperties transactionProperties;

    public TransactionService(TransactionProperties transactionProperties, ObjectMapper objectMapper) {
        this.transactionProperties = transactionProperties;
        this.objectMapper = objectMapper;
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        // 加载商户私钥（privateKey：私钥字符串）
        PrivateKey merchantPrivateKey = null;
        try {
            merchantPrivateKey = PemUtil
                    .loadPrivateKey(new FileInputStream(ResourceUtils.getFile("classpath:apiclient_key.pem")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                new WechatPay2Credentials(transactionProperties.getMchId(), new PrivateKeySigner(transactionProperties.getMchSerialNo(), merchantPrivateKey)), transactionProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));

        // 初始化httpClient
        httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(transactionProperties.getMchId(), transactionProperties.getMchSerialNo(), merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();
    }

    private final HttpClient httpClient;


    /**
     * 获取平台证书
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public void getPlatformCert() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder(transactionProperties.getRootUrl() + "/certificates");
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet);
        String bodyAsString = EntityUtils.toString(response.getEntity());
        System.out.println(bodyAsString);
    }

    /**
     * 创建订单
     *
     * @throws Exception
     */
    public String createOrder(String outTradeNo, Integer total, String description, String attach) throws IOException {
        return this.createOrder(outTradeNo, total, notifyUrl, description, attach);
    }


    /**
     * 创建订单
     *
     * @throws Exception
     */
    public String createOrder(String outTradeNo, Integer total, String notifyUrl, String description, String attach) throws IOException {
        HttpPost httpPost = new HttpPost(transactionProperties.getRootUrl() + "/pay/transactions/native");
        AmountInfo amount = new AmountInfo(total, "CNY");
        OrderInfo orderInfo = new OrderInfo(transactionProperties.getAppid(), transactionProperties.getMchId(), description, outTradeNo, notifyUrl, amount, attach);
        System.out.println(objectMapper.writeValueAsString(orderInfo));
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(orderInfo), StandardCharsets.UTF_8);

        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                //处理成功
                String object = objectMapper.readTree(EntityUtils.toString(response.getEntity())).get("code_url").textValue();
                return object;
            } else if (statusCode == 204) {
                //处理成功，无返回Body
                return null;
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
                //throw new RuntimeException("request failed");
            }
        }
    }

    /**
     * 需要先通过商户订单号查询到微信的订单号
     *
     * @param outTradeNo 商户自由流水号
     * @throws URISyntaxException
     * @throws IOException
     */
    public OrderResult getOutTradeNo(String outTradeNo) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder(transactionProperties.getRootUrl() + "/pay/transactions/out-trade-no/" + outTradeNo + "?mchid=" + transactionProperties.getMchId());
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");

        //完成签名并执行请求
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String bodyAsString = EntityUtils.toString(response.getEntity());
                return objectMapper.readValue(bodyAsString, OrderResult.class);
            } else if (statusCode == 204) {
                //处理成功，无返回Body
                return null;
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        }
    }

    /**
     * 订单号查询
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public OrderResult getOrder(String wechatTransactionId) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder(transactionProperties.getRootUrl() + "/pay/transactions/id/" + wechatTransactionId + "?mchid=" + transactionProperties.getMchId());
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Content-Language", "zh-CN");
        //完成签名并执行请求
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String bodyAsString = EntityUtils.toString(response.getEntity());
                return objectMapper.readValue(bodyAsString, OrderResult.class);
            } else if (statusCode == 204) {
                //处理成功，无返回Body
                return null;
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        }
    }

    /**
     * 申请退款
     *
     * @param wechatTransactionId 微信支付流水号
     * @param outRefundNo         商家自有设置的流水号
     * @param reason              退款原因
     * @param refund              退款金额
     * @param total               原订单金额
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     */
    public RefundResult applyRefunds(String wechatTransactionId, String outRefundNo, String reason, int refund, int total) throws IOException {
        HttpPost httpPost = new HttpPost(transactionProperties.getRootUrl() + "/refund/domestic/refunds");
        RefundsAmount refundsAmount = new RefundsAmount(refund, total, "CNY");
        RefundInfo refundInfo = new RefundInfo(wechatTransactionId, outRefundNo, reason, refundsAmount);
        System.out.println(objectMapper.writeValueAsString(refundInfo));
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(refundInfo), StandardCharsets.UTF_8);

        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Language", "zh-CN");

        //完成签名并执行请求
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                //处理成功
                String bodyAsString = EntityUtils.toString(response.getEntity());
                System.out.println(bodyAsString);
                return objectMapper.readValue(bodyAsString, RefundResult.class);
            } else if (statusCode == 204) {
                //处理成功，无返回Body
                System.out.println("success");
                return null;
            } else if (statusCode == 403) {
                //不支持退款
                throw new FailException(response.getStatusLine().getReasonPhrase());
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        }
    }

    /**
     * 查询退款
     */
    public void scoutRefunds(String outRefundNo) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder(transactionProperties.getRootUrl() + "/refund/domestic/refunds/" + outRefundNo);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet);

        String bodyAsString = EntityUtils.toString(response.getEntity());
        System.out.println(bodyAsString);
    }


    /**
     * 关闭订单
     */
    public void closeOrder(String wechatTransactionId) throws IOException {
        HttpPost httpPost = new HttpPost(transactionProperties.getRootUrl() + "/pay/transactions/out-trade-no/" + wechatTransactionId + "/close");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("mchid", transactionProperties.getMchId());
        objectMapper.writeValue(bos, rootNode);
        httpPost.setEntity(new StringEntity(bos.toString(StandardCharsets.UTF_8.name())));
        //完成签名并执行请求
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                //处理成功
                String bodyAsString = EntityUtils.toString(response.getEntity());
                System.out.println(bodyAsString);
            } else if (statusCode == 204) {
                //处理成功，无返回Body
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        }
    }

    /**
     * 申请交易账单
     *
     * @throws Exception
     */
    public void tradeBill() throws Exception {
        //请求URL
        URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/bill/tradebill");
        uriBuilder.setParameter("bill_date", "2020-11-09");
        uriBuilder.setParameter("bill_type", "ALL");

        //完成签名并执行请求
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        }
    }

    /**
     * 下载交易账单
     *
     * @param download_url
     * @throws Exception
     */
    public void downloadUrl(String download_url) throws Exception {
        //请求URL
        //账单文件的下载地址的有效时间为30s
        URIBuilder uriBuilder = new URIBuilder(download_url);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");

        //执行请求
        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }

}
