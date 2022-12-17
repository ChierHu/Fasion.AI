package ai.fasion.fabs.vulcan.token.entity;

import java.util.UUID;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/21 12:03
 * @since JDK 1.8
 */
public class Token {
    /**
     * 生成时间,ms时间戳
     */
    private long generateTime;
    /**
     * 过期时间,ms时间戳
     */
    private long expireTime;
    /**
     * 有效期，ms
     */
    private long expireIn;
    /**
     * 令牌
     */
    private String accessToken;

    /**
     * 创建token
     *
     * @param expireIn
     * @return
     */
    public static Token createToken(long expireIn) {
        Token token = new Token();
        token.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
        token.setGenerateTime(System.currentTimeMillis());
        token.setExpireIn(expireIn);
        token.setExpireTime(token.getGenerateTime() + expireIn);
        return token;
    }

    public long getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(long expireIn) {
        this.expireIn = expireIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
