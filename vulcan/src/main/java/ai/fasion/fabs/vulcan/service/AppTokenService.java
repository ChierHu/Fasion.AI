package ai.fasion.fabs.vulcan.service;

import ai.fasion.fabs.vulcan.token.entity.AppTokenGetParam;
import ai.fasion.fabs.vulcan.token.entity.Token;
import ai.fasion.fabs.vulcan.token.manage.AppTokenManage;
import ai.fasion.fabs.vulcan.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/21 19:15
 * @since JDK 1.8
 */
@Service
public class AppTokenService {
    @Autowired
    private AppTokenManage appTokenManage;
    @Autowired
    private RedisUtil redisUtil;

    public Token buildToken(String appKey, String random, String sign) throws Exception {
        //校验参数不能为空。
        String appSecret = validateApp(appKey);
        AppTokenGetParam appTokenGetParam = new AppTokenGetParam();
        appTokenGetParam.setAppKey(appKey);
        appTokenGetParam.setAppSecret(appSecret);
        appTokenGetParam.setRandom(random);
        appTokenGetParam.setSign(sign);
        Token token = appTokenManage.build(appTokenGetParam);
        return token;
    }

    /**
     * 校验appKey的合法性，同时返回这个app相关的一些属性，比如appSecret。
     *
     * @param appKey
     * @return
     */
    private String validateApp(String appKey) throws Exception {
        Map<String, String> appInfo = null;
        try {
            appInfo = (Map<String, String>) redisUtil.get("appKey:" + appKey);
        } catch (Exception e) {
        }
        //查询当前key的密码
        if (appInfo != null && appInfo.size() > 0) {
            return appInfo.get("appSecret");
        } else {
            //todo 到数据库查询
//            throw new Exception("appKey不存在");
            return "0c128efdc2ef45c1a7be39462701e65b";
        }
    }

}
