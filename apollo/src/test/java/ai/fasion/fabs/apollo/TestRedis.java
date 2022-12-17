package ai.fasion.fabs.apollo;

import ai.fasion.fabs.apollo.assets.pojo.Ticket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/24 19:58
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void show() {
//        Object demo_list = redisTemplate.opsForList().rightPop("demo_list", 10, TimeUnit.SECONDS);
//        System.out.println(demo_list);
//        redisTemplate.opsForList().leftPush("demo_list", "12313");
//        redisTemplate.opsForList().leftPush("demo_list", "456456");
//        demo_list = redisTemplate.opsForList().rightPop("demo_list");
//        System.out.println(demo_list);
//        Object demo_list = redisTemplate.
        Ticket ticket = new Ticket();
        ticket.setType("type");
        ticket.setPath("path");
        ticket.setUid("1234567890");


//        redisTemplate.opsForValue().set("demo", ticket);

        Ticket demo = (Ticket)redisTemplate.opsForValue().get("demo");
        System.out.println(demo);
    }


}
