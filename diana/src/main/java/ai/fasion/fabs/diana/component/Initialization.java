package ai.fasion.fabs.diana.component;
import ai.fasion.fabs.diana.constant.KsOssConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Function: 初始化操作
 *
 * @author miluo
 * Date: 2021/6/9 17:34
 * @since JDK 1.8
 */
@Component
public class Initialization {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final KsOssConstant ksOssConstant;

    public Initialization(KsOssConstant ksOssConstant) {
        this.ksOssConstant = ksOssConstant;
    }

    public String getAK() {
        return ksOssConstant.getAccessKey();
    }

    public String getSK() {
        return ksOssConstant.getSecretKey();
    }
}
