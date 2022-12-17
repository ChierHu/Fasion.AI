package ai.fasion.fabs.apollo.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Function: 前缀配置
 *
 * @author yangzhiyuan Date: 2021-01-22 15:58:52
 * @since JDK 1.8
 */
@Component
@ConfigurationProperties(prefix = "apollo.redis.prefix")
public class RedisKeyPrefixConstant {

    public String ticketCode;

    public String authCode;

    public String phoneCode;

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
}
