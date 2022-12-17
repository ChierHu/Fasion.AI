package ai.fasion.fabs.apollo;

import ai.fasion.fabs.apollo.auth.vo.UserCodeVO;
import ai.fasion.fabs.apollo.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/29 11:53
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestsEnvironment {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AuthService authService;

    @Test
    public void demo() {
        Environment evn = applicationContext.getEnvironment();
        String[] activeProfiles = evn.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            System.out.println(activeProfile);
        }
    }

    @Test
    public void show() {
        UserCodeVO userCode = new UserCodeVO();
        userCode.setPhone("13683398319");
        authService.developSendVerificationCode(userCode);
    }
}
