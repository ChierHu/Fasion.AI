package ai.fasion.fabs.apollo.auth;

import ai.fasion.fabs.apollo.auth.vo.*;
import ai.fasion.fabs.apollo.config.annotation.IgnoreUserStatus;
import ai.fasion.fabs.apollo.domain.ResponseEmptyEntity;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Function: 用户授权
 *
 * @author miluo
 * Date: 2021/5/25 11:33
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/auth")
@Api(tags = "授权")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${apollo.enable.captcha}")
    private boolean sendRealCaptcha;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ApiOperation("发送短信验证码")
    @PostMapping(value = "/code")
    public ResponseEntity<Object> sendMessage(@Validated @RequestBody UserCodeVO userCodeVO) {
        if (sendRealCaptcha) {
            authService.sendVerificationCode(userCodeVO);
        } else {
            authService.developSendVerificationCode(userCodeVO);
        }
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }

    @ApiOperation("登录")
    @PostMapping(value = "/login")
    public ResponseEntity<RetLoginUserInfoVO> login(@Validated @RequestBody LoginUserLoginVO loginUserLoginVO) throws Exception {
        RetLoginUserInfoVO userInfo = authService.login(loginUserLoginVO);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @ApiOperation("登出")
    @PostMapping(value = "/logout")
    public ResponseEntity<Object> logout() {
        authService.logout();
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }

    @ApiOperation("添加用户申请试用信息")
    @IgnoreUserStatus
    @PostMapping(value = "/apply")
    public ResponseEntity<Object> apply(@RequestBody Map<String, Object> map) {
        //获取user_info的id
        String id = AppThreadLocalHolder.getUserId();
        try {
            int count = authService.updateUserInfoById(map, id);
            if (count == 0) {
                return new ResponseEntity<>("添加用户试用信息失败！", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }

}
