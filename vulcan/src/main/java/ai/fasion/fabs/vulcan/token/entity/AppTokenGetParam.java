package ai.fasion.fabs.vulcan.token.entity;

/**
 * Function: app token 请求参数
 *
 * @author miluo
 * Date: 2021/5/21 18:53
 * @since JDK 1.8
 */
public class AppTokenGetParam {
    private String appKey;
    private String random;
    private String sign;
    private String appSecret;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
