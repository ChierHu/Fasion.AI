package ai.fasion.fabs.vulcan.token.storage;

import ai.fasion.fabs.vulcan.token.entity.Token;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * Function: token mapper
 *
 * @author miluo
 * Date: 2021/5/21 12:02
 * @since JDK 1.8
 */
public interface TokenStorage {

    /**
     * 生成或查询token
     *
     * @param key    唯一用来识别token的key
     * @param values token对应的业务参数
     * @return
     */
    Token generalOrQuery(String key, Map<String, String> values) throws JsonProcessingException;

    /**
     * 查询token，查询token对应的业务参数
     *
     * @param token
     * @return
     */
    Map<String, String> tokenQuery(String token);
}
