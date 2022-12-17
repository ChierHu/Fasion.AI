package ai.fasion.fabs.vulcan.token.manage;

import ai.fasion.fabs.vesta.utils.SHA256Util;
import ai.fasion.fabs.vulcan.token.entity.AppParam;
import ai.fasion.fabs.vulcan.token.entity.AppTokenGetParam;
import ai.fasion.fabs.vulcan.token.entity.Token;
import ai.fasion.fabs.vulcan.token.storage.TokenStorage;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.ks3.utils.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataInput;
import java.io.IOException;
import java.util.Map;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/21 18:53
 * @since JDK 1.8
 */
@Component
public class AppTokenManage implements TokenManage<AppTokenGetParam, AppParam> {

    @Autowired
    private TokenStorage tokenStorage;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Token build(AppTokenGetParam getTokenParam) throws Exception {
        signValidate(getTokenParam);
        String key = getSaveKey(getTokenParam);
        AppParam param = new AppParam();
        param.setAppKey(getTokenParam.getAppKey());
        return tokenStorage.generalOrQuery(key, objectMapper.readValue(objectMapper.writeValueAsString(param), Map.class));
    }

    @Override
    public AppParam validate(String token) throws IOException {
        Map<String, String> saveValues = tokenStorage.tokenQuery(token);
        if (saveValues != null && saveValues.size() > 0) {
            return objectMapper.readValue(objectMapper.writeValueAsString(saveValues), AppParam.class);
        }
        return null;
    }


    private void signValidate(AppTokenGetParam getTokenParam) throws Exception {
        String sign = SHA256Util.getSHA256(getTokenParam.getAppKey() + getTokenParam.getRandom() + getTokenParam.getAppSecret());
        if (!sign.equals(getTokenParam.getSign())) {
            throw new Exception("签名验证失败");
        }
    }

    private String getSaveKey(AppTokenGetParam getTokenParam) {
        return "appKey-token:" + getTokenParam.getAppKey();
    }
}
