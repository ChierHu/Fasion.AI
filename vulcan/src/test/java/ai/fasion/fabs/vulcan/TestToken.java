package ai.fasion.fabs.vulcan;

import ai.fasion.fabs.vesta.utils.SHA256Util;
import ai.fasion.fabs.vulcan.service.AppTokenService;
import ai.fasion.fabs.vulcan.token.entity.AppParam;
import ai.fasion.fabs.vulcan.token.entity.Token;
import ai.fasion.fabs.vulcan.token.manage.AppTokenManage;
import ai.fasion.fabs.vulcan.utils.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/21 19:16
 * @since JDK 1.8
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestToken {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppTokenService appTokenService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AppTokenManage appTokenManage;

    /**
     * 调用方去获取token。
     *
     * @throws Exception
     */
    @Test
    public void getToken() throws Exception {
        String appKey = "1a748b70d0cb4a8a8e37e959f4a4f1e6";
        String appSecret = "0c128efdc2ef45c1a7be39462701e65b";
        //String random = UUID.randomUUID().toString();
        String random = "db35c704-d6ae-46ef-bb05-1240ad34df16";
        random = random.replace("-", "");
        String sign = SHA256Util.getSHA256(appKey + random + appSecret);

        redisUtil.set("appKey:1a748b70d0cb4a8a8e37e959f4a4f1e6", "{\"appSecret\":\"0c128efdc2ef45c1a7be39462701e65b\"}");

//        System.out.println(sign);
//
//        //这边应该是API的调用，而不是方法调用，简单模拟一下就行了。
        Token token = appTokenService.buildToken(appKey, random, sign);
        System.out.println(objectMapper.writeValueAsString(token));
    }


    /**
     * 模拟API调用
     */
    @Test
    public void businssCall() throws Exception {
        String accessToken = "2e813864b7df48888129647510c43f3d";

        AppParam appParam = appTokenManage.validate(accessToken);
//        System.out.println(JSONObject.toJSONString(appParam));
        //拿到token对应的参数，接下来就可以进一步处理了。TODO
    }
}
