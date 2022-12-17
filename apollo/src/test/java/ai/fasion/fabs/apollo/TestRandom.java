package ai.fasion.fabs.apollo;

import ai.fasion.fabs.apollo.auth.AuthMapper;
import ai.fasion.fabs.apollo.auth.AuthService;
import ai.fasion.fabs.vesta.Utils;
import ai.fasion.fabs.vesta.utils.SnowflakeUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/7/30 17:26
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRandom {

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private AuthService authService;


    /**
     * userid初始值
     */
    @Value("${apollo.uid.seed}")
    private Integer seed;

    /**
     * 步长
     */
    @Value("${apollo.uid.step}")
    private Integer step;

    @Test
    public void testRandom() {
        //1.获取库里面最新的userId
        Integer currentDataBaseNewestUserId = authMapper.getNewestUserId();
        //2. 判断newestUserId是否存在
        if (null == currentDataBaseNewestUserId) {
            //不存在时，证明数据库里是空的，那么需要获取配置文件中的初始值用于
            currentDataBaseNewestUserId = seed;
        }
        //3. 初始化随机实例
        Random random = new Random();
        currentDataBaseNewestUserId = 2147483647;
        int newGenerationUserId;
        //4. 从配置文件中获取步长信息
        //5. 生成一个新的userId
        do {
            //8. 如果步长为0，那么直接返回500错误，证明已经不能再继续增长
            if (step <= 0) {
                throw new RuntimeException("无法创建新用户id");
            }
            newGenerationUserId = random.nextInt(step) + (currentDataBaseNewestUserId + 1);
            //6. 判断新生成的userId是否超过int最大上限
            //7. 如果超过最大上限，获取数据库中最新userId和最大上限之间的差值，作为新的步长
            step = Integer.MAX_VALUE - (currentDataBaseNewestUserId + step);
            //9. 如果不长大于0，那么直接返回新userId
            System.out.println(newGenerationUserId);
        } while (true);

    }

    @Test
    public void show() {
//        for (int i = 0; i < 100; i++) {
//            System.out.println(authService.createNewUserId());
//        }
//        System.out.println(authService.createNewUserId());

        System.out.println(SnowflakeUtil.nextId());
        System.out.println(Utils.hashId(SnowflakeUtil.nextId()));
        System.out.println(Utils.hashId(SnowflakeUtil.nextId()));
        System.out.println(Utils.hashId(SnowflakeUtil.nextId()));
        System.out.println(Utils.hashId(SnowflakeUtil.nextId()));
    }
}
