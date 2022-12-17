package ai.fasion.fabs.apollo;

import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.apollo.assets.pojo.Policy;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.ks3.dto.Authorization;
import com.ksyun.ks3.dto.Bucket;
import com.ksyun.ks3.dto.Ks3ObjectSummary;
import com.ksyun.ks3.dto.ObjectListing;
import com.ksyun.ks3.http.HttpMethod;
import com.ksyun.ks3.http.Request;
import com.ksyun.ks3.utils.AuthUtils;
import com.ksyun.ks3.utils.Base64;
import com.ksyun.ks3.utils.DateUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.util.*;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/24 11:53
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOSS {

    @Autowired
    private KsOssConstant ksOssConstant;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String AK = "AKLTMrohfR2HT1e91vHJFNQiXw";

    private static final String SK = "OJWIWhdKMqXSYrWJFGS0lJNe1RHHGCiiVKfBOr8jnuhRhLByLmAw+4dA8HiAbWV5mA==";

    public static final String KS3OSS = "https://ks3-cn-beijing.ksyun.com";


    /**
     * 要显示数据时，前端过来获取签名才能访问
     * 金山云对象存储下发签名验证
     * 这个是专门用于数据显示的
     * https://docs.ksyun.com/documents/905#2
     *
     * @throws SignatureException
     */
    @Test
    public void getSignatureToAccess() throws SignatureException {
        //此示例为分块上传第一步Initiate Multipart Upload
        Authorization auth = new Authorization(AK, SK);
        Request request = new Request();
        request.setMethod(HttpMethod.POST);
        request.setKey("demo/rsdl.mp4");
        request.setEndpoint("ks3-cn-beijing.ksyun.com");
        request.setBucket("fasion-devel");

        //设置url参数
        Map<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("uploads", null);
        request.setQueryParams(queryParamMap);

        //设置header
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "binary/octet-stream");
        String dateStr = DateUtils.convertDate2Str(new Date(), DateUtils.DATETIME_PROTOCOL.RFC1123);
        headers.put("Date", dateStr);
        request.setHeaders(headers);

        //传入请求参数，从服务端获取签名，AuthUtils工具类在java sdk中
        String signature = AuthUtils.calcAuthorization(auth, request);
        System.out.println(signature);


        String url = "http://" + request.getEndpoint() + "/" + request.getBucket() + "/" + request.getKey() + "?uploads";
        System.out.println(url);
        //实际发起请求
        HttpPost httpPost = new HttpPost(url);
        //下面的header传入内容需要和计算签名时保持一致
        httpPost.addHeader("Date", dateStr);
        httpPost.addHeader("Authorization", signature);
        httpPost.addHeader("Content-Type", "binary/octet-stream");

        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse responseBody = httpclient.execute(httpPost);
//            System.out.println(JSON.toJSONString(responseBody));
            System.out.println(responseBody.getStatusLine());


            InputStream content = responseBody.getEntity().getContent();
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建policy
     */
    @Test
    public void createPolicy() {
        Policy policy = new Policy();
        try {
            String policyJson = objectMapper.writeValueAsString(policy);
            byte[] encode = Base64.encode(policyJson.getBytes());
            String s = AuthUtils.calcSignature(SK, Arrays.toString(encode));
            System.out.println(s);
        } catch (JsonProcessingException | SignatureException e) {
            e.printStackTrace();
        }

//        Signature = Base64(HMAC-SHA1(YourSecretKey, UTF-8-Encoding-Of(StringToSign ) ) );
    }

    @Test
    public void iteratorFile() {
        List<Bucket> buckets = KsOssUtil.getInstance().listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.toString());
        }

    }

    @Test
    public void show() {
//        System.out.println(KsOssUtil.getInstance().shiftName("starlight", "dev/configure/defaultAvatar.png"));
//        System.out.println(KsOssUtil.getInstance().shiftName("test-data", "data/c0/374446957961217/assets/face/source/2021-05-29/375455795077120/demo.jpeg"));
        System.out.println(KsOssUtil.getInstance().shiftName("test-data", "zip/2021-06-01/CE9C1342AD6581564BBAB17E8DA8A9C6.zip"));
//        Calendar nowTime = Calendar.getInstance();
//        //30分钟后的时间
//        nowTime.add(Calendar.MINUTE, 30);
//
//        String format1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
//                .withZone(ZoneOffset.UTC)
//                .format(Instant.now());
//
//        String format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
//                .withZone(ZoneOffset.UTC)
//                .format(Instant.ofEpochMilli(nowTime.getTimeInMillis()));
//
//
////        LocalDateTime localLocalDateTime = LocalDateTime.now().plusMinutes(30);
////        localLocalDateTime.format(format);
//        System.out.println(format1);
//        System.out.println(format);


//        PostObjectFormFields postObjectFormFields = KsOssUtil.getInstance().postObjectSimple();
//        System.out.println(postObjectFormFields);
    }

    @Test
    public void list() {
        System.out.println(ksOssConstant.getBucketName());
       List<ObjectListing> objectListing = KsOssUtil.getInstance().listObjectsWithPrefix(ksOssConstant.getBucketName(), "data/system/assets");
        for (ObjectListing listing : objectListing) {
            System.out.println(listing);
        }

       for (Ks3ObjectSummary objectSummary : objectListing.get(0).getObjectSummaries()) {
            System.out.println(objectSummary);
        }



    }


}