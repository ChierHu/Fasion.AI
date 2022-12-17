package ai.fasion.fabs.diana;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 */
@SpringBootApplication
@ComponentScan(basePackages = {"ai.fasion.fabs.diana", "ai.fasion.fabs.mercury"})
public class DianaApplication {
    static final Logger logger = LoggerFactory.getLogger(DianaApplication.class);

    public static void main(String[] args) {
        logger.info("(^-^)……star……(^_^)");
        SpringApplication.run(DianaApplication.class, args);
    }
}
