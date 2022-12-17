package ai.fasion.fabs.vesta.utils;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Function: 工具类MD5加密
 *
 * @author yangzhiyuan Date: 2021-01-20 16:02:06
 * @since JDK 1.8
 */
public class MD5Utils {
  private static MessageDigest mdigest = null;
  private static final char[] digits = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };

  private static MessageDigest getMdInst() {
    if (null == mdigest) {
      try {
        mdigest = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }
    return mdigest;
  }

  public static String encode(@NonNull String s) {
    try {
      byte[] bytes = s.getBytes();
      getMdInst().update(bytes);
      byte[] md = getMdInst().digest();
      int j = md.length;
      char[] str = new char[j * 2];
      int k = 0;
      for (byte byte0 : md) {
        str[k++] = digits[byte0 >>> 4 & 0xf];
        str[k++] = digits[byte0 & 0xf];
      }
      return new String(str);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String encode16(String s) {
    String encode = encode(s);
    if (StringUtils.isNotBlank(encode)) {
      return encode.substring(8, 24);
    }
    return null;
  }

  /**
   * 盐值加密
   *
   * @param str 待加密数据
   * @param salt 盐
   * @return
   */
  public static String encryptSalt(String str, String salt) {
    String s = str + salt;
    return encode(s);
  }
}
