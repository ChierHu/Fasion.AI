package ai.fasion.fabs.vesta.service;

import ai.fasion.fabs.vesta.constant.Consts;
import ai.fasion.fabs.vesta.model.SendSmsResult;
import ai.fasion.fabs.vesta.utils.KsMessageUtil;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Function: 发送短信服务
 *
 * @author yangzhiyuan Date: 2021-01-21 14:06:13
 * @since JDK 1.8
 */
public class SmsService {

    private final String ak;

    private final String sk;

    private final String signName;

    public SmsService(String ak, String sk, String signName) {
        if (KsMessageUtil.isNullOrEmpty(ak) || KsMessageUtil.isNullOrEmpty(sk)) {
            throw new IllegalArgumentException("ak或sk不能为空");
        }
        this.ak = ak;
        this.sk = sk;
        this.signName = signName;
    }

    public SendSmsResult sendSms(String mobile, String tplId, Map<String, String> tplParams, String Content, String SmsType) {
        try {
            HttpRequest request = HttpRequest.post(Consts.SMS_HOST).accept("application/json").contentType("application/x-www-form-urlencoded;charset=utf8");
            Map<String, String> params = buildSendSmsParams(mobile, tplId, tplParams, Content, SmsType);
            Map<String, String> finalParams = KsMessageUtil.sign(ak, sk, Consts.SERVICE, Consts.REGION, params);
            for (Map.Entry<String, String> entry : finalParams.entrySet()) {
                request.form(entry.getKey(), entry.getValue());
            }

            HttpResponse response = request.send();
            SendSmsResult sendSmsResult = KsMessageUtil.fromJson(response.bodyText(), SendSmsResult.class);
            sendSmsResult.setCode(response.statusCode());
            return sendSmsResult;
        } catch (Exception e) {
            throw new RuntimeException("sensSms Occur Error.", e);
        }
    }

    private Map<String, String> buildSendSmsParams(String mobile, String tplId, Map<String, String> tplParams, String Content, String SmsType) {
        Map<String, String> params = new HashMap<>();
        params.put("Action", Consts.ACTION_SEND_SMS);
        params.put("Version", Consts.VERSION);
        params.put("Mobile", mobile);
        params.put("SignName", this.signName);
        if (!tplId.isEmpty()) {
            params.put("TplId", tplId);
            params.put("TplParams", KsMessageUtil.toJson(tplParams));
        } else {
            params.put("Content", Content);
            params.put("SmsType", SmsType);
        }
        return params;
    }

    private Map<String, String> buildFetchSmsReportParams(Integer pullSize) {
        Integer finalPullSize = (pullSize > 500) ? 500 : pullSize;
        Map<String, String> params = new HashMap<>();
        params.put("Action", Consts.ACTION_PULL_SMS_REPORT);
        params.put("Version", Consts.VERSION);
        params.put("Size", String.valueOf(finalPullSize));
        return params;
    }

    public String fetchSmsReport(int pullSize) {
        try {
            HttpRequest request = HttpRequest.get(Consts.SMS_HOST).accept("application/json");
            Map<String, String> params = buildFetchSmsReportParams(pullSize);
            Map<String, String> finalParams = KsMessageUtil.sign(ak, sk, Consts.SERVICE, Consts.REGION, params);
            request.query(finalParams);
            HttpResponse response = request.send();
            return response.bodyText();
        } catch (Exception e) {
            throw new RuntimeException("fetchSmsReport Occur Error.", e);
        }
    }
}
