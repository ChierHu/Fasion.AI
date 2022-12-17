package ai.fasion.fabs.apollo;

import ai.fasion.fabs.vesta.utils.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IdSnowFlakeTest {

    @Autowired
    private IdGenerator idGenerator;

    @Test
    public void testIdSnowFlake(){
        // 使用时候 workerid  datacenterid 要么从 系统变量里取， 传空就默认 取ip,mac等信息中取位
        // 位数信息 1 位符号  41位 时间戳 已占用42 位   如果要求 48位 或者 64位  wokerid，datacenterid,seq序列号  这3个参数位数需要考量
        //支持 机器id workid 位数为0
        // 此实例 使用时候可以在以@bean 方式注入
      //  IdGenerator shortIdGenerator = new IdGenerator(0, 0, false, 0, null, 0, 0, 6);
        for (int j = 0; j < 1000; j++) {
            System.out.println(System.currentTimeMillis() + " " + idGenerator.nextIdRandom());
        }
    }
}
