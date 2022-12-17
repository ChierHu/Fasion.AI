package ai.fasion.fabs.apollo.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Function:金山云oss常量
 *
 * @author miluo
 * Date: 2021/4/2 13:37
 * @since JDK 1.8
 */
@Component
@ConfigurationProperties(prefix = "apollo.ks3")
public class KsOssConstant {


    private String endpoint;

    /**
     * bucket name
     */
    private String bucketName;

    /**
     * bucket 域名
     */
    private String bucketDomain;

    /**
     * ak
     */
    private String accessKey;

    /**
     * sk
     */
    private String secretKey;

    /**
     *默认头像地址
     */
    private String defaultAvatar;

    /**
     *背景图默认地址
     */
    private String backgroundImageUrl;

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getDefaultAvatar() {
        return defaultAvatar;
    }

    public void setDefaultAvatar(String defaultAvatar) {
        this.defaultAvatar = defaultAvatar;
    }

    public String getBucketDomain() {
        return bucketDomain;
    }

    public void setBucketDomain(String bucketDomain) {
        this.bucketDomain = bucketDomain;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String ak) {
        this.accessKey = ak;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String sk) {
        this.secretKey = sk;
    }
}
