package ai.fasion.fabs.apollo;

import ai.fasion.fabs.apollo.tasks.TaskMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/6/1 16:20
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPgJson {

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 查询json数据
     */
    @Test
    public void select() {
//        List<TaskDTO> taskDTOS = taskMapper.selectTestJosn();
//        System.out.println(taskDTOS);
    }
}
