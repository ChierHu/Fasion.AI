package ai.fasion.fabs.apollo.config;

import ai.fasion.fabs.vesta.utils.IdGenerator;
import liquibase.pro.packaged.C;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdConfig {

    //配置id生成实例 
    @Bean
    public IdGenerator idGenerator(){
        IdGenerator shortIdGenerator = new IdGenerator(0, 0, false, 0, null, 0, 0, 6);

        return shortIdGenerator;
    }
}
