package ai.fasion.fabs.vesta.http;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Function: 获取真实的ip地址
 *
 * @author miluo Date: 2020/3/28 8:59 下午
 * @since JDK 1.8
 */
public class IpUtil {

  private static final String UNKNOWN = "unknown";
  /** 本地地址 */
  private static final String SELF_IP = "127.0.0.1";
  /** ip的长度 */
  private static final int IP_LENGTH = 15;

  private static final String KEYWORD = ",";

  public static String getIpAddr(HttpServletRequest request) {
    String ipAddress = null;
    try {
      ipAddress = request.getHeader("x-forwarded-for");
      if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("Proxy-Client-IP");
      }
      if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("WL-Proxy-Client-IP");
      }
      if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getRemoteAddr();
        if (ipAddress.equals(SELF_IP)) {
          // 根据网卡取本机配置的IP
          InetAddress inet = null;
          try {
            inet = InetAddress.getLocalHost();
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
          ipAddress = inet.getHostAddress();
        }
      }
      // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
      // "***.***.***.***".length()
      if (ipAddress != null && ipAddress.length() > IP_LENGTH) {
        // = 15
        if (ipAddress.indexOf(KEYWORD) > 0) {
          ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
      }
    } catch (Exception e) {
      ipAddress = "";
    }
    // ipAddress = this.getRequest().getRemoteAddr();

    return ipAddress;
  }
}
