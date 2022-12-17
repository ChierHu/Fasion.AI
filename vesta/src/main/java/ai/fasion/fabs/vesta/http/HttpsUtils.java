package ai.fasion.fabs.vesta.http;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Function: Https证书校验工具类
 *
 * @author miluo Date: 2018/9/9 下午5:37
 * @since JDK 1.8
 */
public class HttpsUtils {

  public static class SSLParams {
    public SSLSocketFactory sSLSocketFactory;
    public X509TrustManager trustManager;
  }

  /** 不做证书校验，信任所有证书 */
  public static SSLParams getSslSocketFactoryUnsafe() {
    SSLParams sslParams = new SSLParams();
    try {
      X509TrustManager x509TrustManager = new UnSafeTrustManager();

      // 创建TLS类型的SSLContext对象，that uses our TrustManager
      SSLContext sslContext = SSLContext.getInstance("TLS");

      // 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
      sslContext.init(null, new TrustManager[] {x509TrustManager}, null);

      // Android 4.X 对TLS1.1、TLS1.2的支持
      sslParams.sSLSocketFactory = new Tls12SocketFactory(sslContext.getSocketFactory());
      sslParams.trustManager = x509TrustManager;
      return sslParams;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new AssertionError(e);
    }
  }

  /** 主机名校验方法，请把”192.168.0.10”换成公司主机IP： */
  public static HostnameVerifier getHostnameVerifier() {
    return new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        if ("192.168.0.10".equals(hostname)) {
          return true;
        } else {
          HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
          return hv.verify(hostname, session);
        }
      }
    };
  }

  /** 对服务器证书域名进行强校验 */
  private static class SafeTrustManager implements X509TrustManager {
    private final X509Certificate mCertificate;

    private SafeTrustManager(X509Certificate serverCert) {
      mCertificate = serverCert;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String authType)
        throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String authType)
        throws CertificateException {
      if (x509Certificates == null) {
        throw new IllegalArgumentException("Check Server x509Certificates is null");
      }

      if (x509Certificates.length < 0) {
        throw new IllegalArgumentException("Check Server x509Certificates is empty");
      }

      try {
        for (X509Certificate cert : x509Certificates) {
          // Make sure that it hasn't expired.
          cert.checkValidity();
          // 和App预埋的证书做对比
          cert.verify(mCertificate.getPublicKey());
        }
      } catch (NoSuchAlgorithmException
          | InvalidKeyException
          | NoSuchProviderException
          | SignatureException e) {
        e.printStackTrace();
      }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }

  /** 客户端不对证书做任何验证的做法有很大的安全漏洞。 */
  private static class UnSafeTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[] {};
    }
  }

  /** 自定义SSLSocketFactory ，实现Android 4.X 对TLSv1.1、TLSv1.2的支持 */
  private static class Tls12SocketFactory extends SSLSocketFactory {

    private static final String[] TLS_SUPPORT_VERSION = {"TLSv1.1", "TLSv1.2"};

    final SSLSocketFactory delegate;

    private Tls12SocketFactory(SSLSocketFactory base) {
      this.delegate = base;
    }

    @Override
    public String[] getDefaultCipherSuites() {
      return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
      return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose)
        throws IOException {
      return patch(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
      return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
        throws IOException {
      return patch(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
      return patch(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(
        InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
      return patch(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket patch(Socket s) {
      // 代理SSLSocketFactory在创建一个Socket连接的时候，会设置Socket的可用的TLS版本。
      if (s instanceof SSLSocket) {
        ((SSLSocket) s).setEnabledProtocols(TLS_SUPPORT_VERSION);
      }
      return s;
    }
  }
}
