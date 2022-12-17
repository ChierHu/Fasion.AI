package ai.fasion.fabs.vesta.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Function: 金山短信云Util
 *
 * @author yangzhiyuan Date: 2021-01-21 14:07:02
 * @since JDK 1.8
 */
public class KsMessageUtil {

    private KsMessageUtil() {
    }

    private static final Logger logger = LoggerFactory.getLogger(KsMessageUtil.class);

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC();

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern ENCODED_CHARACTERS_PATTERN;

    static {
        String pattern = Pattern.quote("+") +
                "|" +
                Pattern.quote("*") +
                "|" +
                Pattern.quote("%7E") +
                "|" +
                Pattern.quote("%2F");
        ENCODED_CHARACTERS_PATTERN = Pattern.compile(pattern);
    }

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String encode(Map<String, String> params) {
        Map<String, String> map = new HashMap<>(params);
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        if (map.size() == 0) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        for (String key : keys) {
            String value = map.get(key);
            body.append(key).append('=').append(encode(value, false)).append('&');
        }
        return body.substring(0, body.length() - 1);
    }

    private static String encode(final String value, final boolean path) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, DEFAULT_ENCODING);
            Matcher matcher = ENCODED_CHARACTERS_PATTERN.matcher(encoded);
            StringBuffer buffer = new StringBuffer(encoded.length());
            while (matcher.find()) {
                String replacement = matcher.group(0);

                if ("+".equals(replacement)) {
                    replacement = "%20";
                } else if ("*".equals(replacement)) {
                    replacement = "%2A";
                } else if ("%7E".equals(replacement)) {
                    replacement = "~";
                } else if (path && "%2F".equals(replacement)) {
                    replacement = "/";
                }

                matcher.appendReplacement(buffer, replacement);
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<String, String> sign(
            String ak, String sk, String service, String region, Map<String, String> privateParams) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("Accesskey", ak);
        params.put("Service", service);
        if (privateParams.containsKey("Action")) {
            params.put("Action", privateParams.get("Action"));
        }
        params.put("Timestamp", TIME_FORMATTER.print(System.currentTimeMillis()));
        params.put("SignatureVersion", "1.0");
        params.put("SignatureMethod", "HMAC-SHA256");
        params.put("Region", region);
        params.putAll(privateParams);
        String encodeParam = KsMessageUtil.encode(params);
        logger.info("encodeParam:{} ", encodeParam);
        String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, sk).hmacHex(encodeParam);
        logger.info("signature:{}", signature);
        params.put("Signature", signature);
        return params;
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
     */
    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("write to json string error:" + object, e);
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     *
     * <p>如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
     *
     * <p>如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String, JavaType)
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException("parse json string error:" + jsonString, e);
        }
    }
}
