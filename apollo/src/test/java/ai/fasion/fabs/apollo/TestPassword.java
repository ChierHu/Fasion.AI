package ai.fasion.fabs.apollo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/7/5 20:06
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPassword {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void show() {
        System.out.println();

        String oldPassword = passwordEncoder.encode("dfsiosd123123");


        System.out.println("比对结果：" + passwordEncoder.matches("dfsiosd123123", oldPassword));
        System.out.println("比对结果：" + passwordEncoder.matches("dfsiosd12312", oldPassword));
    }

}
