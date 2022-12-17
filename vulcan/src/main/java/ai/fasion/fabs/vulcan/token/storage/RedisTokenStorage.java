package ai.fasion.fabs.vulcan.token.storage;

import ai.fasion.fabs.vulcan.token.entity.Token;
import ai.fasion.fabs.vulcan.utils.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Function: redis实现
 *
 * @author miluo
 * Date: 2021/5/21 16:09
 * @since JDK 1.8
 */
@Component
public class RedisTokenStorage implements TokenStorage {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 过期时间 ms
     */
    private long expireIn = 2 * 3600 * 1000L;


    @Override
    public Token generalOrQuery(String key, Map<String, String> values) throws JsonProcessingException {
        Token token;
        Map<String, String> tokenMap = (Map<String, String>) redisUtil.get(key);
        if (tokenMap == null || tokenMap.size() == 0) {
            token = Token.createToken(expireIn);
            tokenMap = new HashMap<>();
            tokenMap.put("generateTime", String.valueOf(token.getGenerateTime()));
            tokenMap.put("expireTime", String.valueOf(token.getExpireTime()));
            tokenMap.put("expireIn", String.valueOf(token.getExpireIn()));
            tokenMap.put("accessToken", String.valueOf(token.getAccessToken()));
            redisUtil.set(key, tokenMap, expireIn);
            redisUtil.set(getTokenKey(token.getAccessToken()), values, expireIn);
        } else {
            token = objectMapper.readValue(tokenMap.toString(), Token.class);
        }
        return token;
    }

    @Override
    public Map<String, String> tokenQuery(String token) {
        return (Map<String, String>) redisUtil.get(getTokenKey(token));
    }

    private String getTokenKey(String token) {
        return "token:" + token;
    }
}
