package ai.fasion.fabs.diana.component;

import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/28 18:33
 * @since JDK 1.8
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Initialization initialization;

    public MyApplicationRunner(Initialization initialization) {
        this.initialization = initialization;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("初始化金山云oss工具类" + initialization.getAK() + "\t" + initialization.getSK());
        // 金山云工具类初始化
        KsOssUtil.init(initialization.getAK(), initialization.getSK());
    }
}
