package ai.fasion.fabs.diana.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Function: 前缀配置
 *
 * @author yangzhiyuan Date: 2021-01-22 15:58:52
 * @since JDK 1.8
 */
@Component
@ConfigurationProperties(prefix = "diana.redis.prefix")
public class RedisKeyPrefixConfig {

    private String authCode;

    private String loginCode;


    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }
}
