package ai.fasion.fabs.mercury.wechat.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Function: 微信配置文件属性
 *
 * @author miluo
 * Date: 2021/9/24 17:09
 * @since JDK 1.8
 */
@Component
public class TransactionProperties {

    /**
     * 根路径
     * "https://api.mch.weixin.qq.com/v3"
     */
    private String rootUrl;

    /**
     * 商户号
     * 1612747535
     */
    private String mchId;

    /**
     * 商户证书序列号
     * 17E1EF6D7D8BCC43D3AA69E4D773F27B0CFA0E41
     */
    private String mchSerialNo;

    /**
     * apiV3Key是String格式的API v3密钥。
     */
    private String apiV3Key;


    /**
     * appid
     * ww4be2bf8fea2489df
     */
    private String appid;


    public String getRootUrl() {
        return rootUrl;
    }

    @Value("${mercury.wechat.root-url}")
    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getMchId() {
        return mchId;
    }

    @Value("${mercury.wechat.mch-id}")
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchSerialNo() {
        return mchSerialNo;
    }

    @Value("${mercury.wechat.mch-serial-no}")
    public void setMchSerialNo(String mchSerialNo) {
        this.mchSerialNo = mchSerialNo;
    }

    public String getApiV3Key() {
        return apiV3Key;
    }

    @Value("${mercury.wechat.api-v3-key}")
    public void setApiV3Key(String apiV3Key) {
        this.apiV3Key = apiV3Key;
    }

    public String getAppid() {
        return appid;
    }

    @Value("${mercury.wechat.app-id}")
    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Override
    public String toString() {
        return "TransactionProperties{" +
                "rootUrl='" + rootUrl + '\'' +
                ", mchId='" + mchId + '\'' +
                ", mchSerialNo='" + mchSerialNo + '\'' +
                ", apiV3Key='" + apiV3Key + '\'' +
                ", appid='" + appid + '\'' +
                '}';
    }
}
