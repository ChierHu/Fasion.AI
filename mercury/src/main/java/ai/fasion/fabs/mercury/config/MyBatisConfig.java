package ai.fasion.fabs.mercury.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Function: mybatis配置文件
 *
 * @author miluo Date: 2018/10/9 上午11:14
 * @since JDK 1.8
 */
@Configuration
public class MyBatisConfig {

  /**
   * 分页插件设定
   *
   * @return 分页插件类
   */
  @Bean
  public PageHelper pageHelper() {
    PageHelper pageHelper = new PageHelper();
    Properties properties = new Properties();
    properties.setProperty("offsetAsPageNum", "true");
    properties.setProperty("rowBoundsWithCount", "true");
    properties.setProperty("reasonable", "true");
    pageHelper.setProperties(properties);
    return pageHelper;
  }
}
