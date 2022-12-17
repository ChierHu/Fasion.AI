package ai.fasion.fabs.apollo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Function:RestTemplate配置文件
 *
 * @author miluo Date: 2020/4/26 12:56 下午
 * @since JDK 1.8
 */
@Configuration
public class RestTemplateConfig {


  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
    for (HttpMessageConverter<?> httpMessageConverter : list) {
      if (httpMessageConverter instanceof StringHttpMessageConverter) {
        ((StringHttpMessageConverter) httpMessageConverter)
            .setDefaultCharset(StandardCharsets.UTF_8);
        break;
      }
    }
    return restTemplate;
  }

  @Bean
  public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(15000);
    factory.setReadTimeout(5000);
    return factory;
  }
}
