package ai.fasion.fabs.vulcan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Function:api接口启动
 *
 * @author miluo
 * Date: 2021/5/21 14:00
 * @since JDK 1.8
 */
@SpringBootApplication
public class VulcanApplication {
    /**
     * Logger实例
     */
    static final Logger logger = LoggerFactory.getLogger(VulcanApplication.class);

    public static void main(String[] args) {
        logger.info("(^-^)……star……(^_^)");
        SpringApplication.run(VulcanApplication.class, args);
    }
}
