package ai.fasion.fabs.apollo.component;

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
        try {
            String systemFaceSourcePath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("assets/face/source")).getPath();
            String systemMattingScenePath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("assets/matting/scene")).getPath();
            String systemMattingSourcePath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("assets/matting/image")).getPath();
            // 上传系统预置背景图
            initialization.scanImages(systemMattingScenePath, Asset.Type.SystemMattingScene);
            // 上传系统预置人物
            initialization.scanImages(systemMattingSourcePath, Asset.Type.SystemMattingImage);
            //上传系统预置头像
            initialization.scanImages(systemFaceSourcePath, Asset.Type.SystemFaceSource);
            //上传fasion中的文件
            String defaultImagePath = Thread.currentThread().getContextClassLoader().getResource("fasion").getPath();
            initialization.scanDefaultImages(defaultImagePath);

            //临时根据新规则迁移uid
            initialization.modifyUserId();
            //输出当前应用程序版本号
            log.info("this application version name is [{}]", initialization.getCurrentApplicationVersionName());
        } catch (Exception e) {
            System.err.println("初始化程序异常：" + e.getMessage());
        }

    }
}
