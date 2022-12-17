package ai.fasion.fabs.vulcan.config.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Function: durid 配置信息
 *
 * @author miluo Date: 2018/9/5 下午4:12
 * @since JDK 1.8
 */
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(
    name = "spring.datasource.type",
    havingValue = "com.alibaba.druid.pool.DruidDataSource",
    matchIfMissing = true)
public class DruidConfig {
  @Bean(name = "druidDataSource")
  @ConfigurationProperties("spring.datasource.druid")
  public DataSource dataSource() {
    return DruidDataSourceBuilder.create().build();
  }
}
