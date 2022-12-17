package ai.fasion.fabs.mercury;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/16 15:25
 * @since JDK 1.8
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MercuryApplication {
    static final Logger log = LoggerFactory.getLogger(MercuryApplication.class);

    public static void main(String[] args) {
        log.info("boot start ... ");
        ApplicationContext context = SpringApplication.run(MercuryApplication.class, args);
        Environment evn = context.getEnvironment();
        String[] activeProfiles = evn.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            log.info("boot environment [{}]", activeProfile);
        }
    }
}
