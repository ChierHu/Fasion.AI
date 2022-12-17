package ai.fasion.fabs.minerva.config;

import ai.fasion.fabs.minerva.constant.KsOssConstant;
import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.expansion.NotFoundException;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/28 18:33
 * @since JDK 1.8
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {
    @Autowired
    private KsOssConstant ksOssConstant;


    @Override
    public void run(ApplicationArguments args) {
        System.out.println("初始化金山云oss工具类" + ksOssConstant.getAccessKey() + "\t" + ksOssConstant.getSecretKey());
        KsOssUtil.init(ksOssConstant.getAccessKey(), ksOssConstant.getSecretKey());
        judgeConfigIsExists();
    }


    /**
     * 检查任务计费需要的配置文件是否存在
     */
    private void judgeConfigIsExists() {
        //1. 判断配置文件是不是存在，不存在就要报异常提示
        if (!KsOssUtil.getInstance().objectExists(ksOssConstant.getBucketName(), "config/config.json")) {
            throw new NotFoundException("初始化时，任务计费所需要的配置文件未找到");
        }
    }
}
