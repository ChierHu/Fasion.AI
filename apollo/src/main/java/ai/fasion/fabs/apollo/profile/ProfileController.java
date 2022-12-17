package ai.fasion.fabs.apollo.profile;

import ai.fasion.fabs.apollo.config.annotation.CurrentUserInfo;
import ai.fasion.fabs.apollo.config.annotation.IgnoreUserStatus;
import ai.fasion.fabs.apollo.domain.ResponseEmptyEntity;
import ai.fasion.fabs.apollo.auth.vo.UserInfoVO;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import com.ksyun.ks3.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Function: 用户信息
 *
 * @author miluo
 * Date: 2021/5/25 11:33
 * @since JDK 1.8
 */
@RestController
@Api(tags = "用户信息")
public class ProfileController {
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation("获取个人资料")
    @IgnoreUserStatus
    @GetMapping("/me")
    public ResponseEntity<UserInfoVO> detail() {
        UserInfoVO userInfoVO = userService.detail(AppThreadLocalHolder.getUserId());
        return new ResponseEntity<>(userInfoVO, HttpStatus.OK);
    }

    @ApiOperation("更新个人资料")
    @PatchMapping("/me")
    public ResponseEntity<Object> update(@Validated @RequestBody UserInfoVO userInfoVO) {
        userInfoVO.setId(AppThreadLocalHolder.getUserId());
        userService.updateUserInfo(userInfoVO);
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }

    /**
     * 更新手机号，需要先验证原先手机号
     * 再验证新手机号
     *
     * @return
     */
    @ApiOperation("更新手机号")
    @PutMapping("/access/phone")
    public ResponseEntity<Object> updatePhone(@CurrentUserInfo UserInfoDO userInfo, @RequestParam("original_phone") String originalPhone, @RequestParam("original_code") String originalCode, @RequestParam("new_phone") String newPhone, @RequestParam("new_code") String newCode) {
        //1. 先用原来手机号发送短信验证码
        //2.将原来手机号和验证码进行查询
        //3.匹配不上的话，返回原手机号验证失败,
        //4.如果匹配上的话，将将新手机号和验证码进行匹配
        //5.匹配不上，返回新手机号验证失败
        //6.匹配上的话，修改成功

        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }

    /**
     * 更新邮箱号需要先往原先邮箱发送一封邮件
     * 通过后输入新邮箱
     *
     * @return
     */
    @ApiOperation("更新邮箱号")
    @PutMapping("/access/email")
    public ResponseEntity<Object> updateEmail() {


        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }

    @ApiOperation("更新密码")
    @PutMapping("/access/password")
    public ResponseEntity<Object> updatePassword(@RequestBody String newPassword, @RequestParam(value = "code", required = false) String code, @RequestParam(value = "pass", required = false) String pass) {
        try {
            if (!StringUtils.isNullOrEmpty(pass) && !StringUtils.isNullOrEmpty(newPassword)) {
                //通过原来密码修改密码
                userService.updatePassword(AppThreadLocalHolder.getUserId(), pass, newPassword);
            } else if (!StringUtils.isNullOrEmpty(code)) {
                //通过手机验证码修改密码
                userService.forgetPassword(code, newPassword);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }


    @GetMapping("/demo")
    public ResponseEntity<ResponseEmptyEntity> demo() {
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }



    @PutMapping("/updateProfile")
    public ResponseEntity<Object> updateProfile(
                                                             @RequestParam("profile") MultipartFile profile) {
        String id = AppThreadLocalHolder.getUserId();
        try {
            userService.updateProfileImage(id, profile);
        }catch (Exception e){
            throw new FailException(e.getMessage());
        }
        return new ResponseEntity<>("更新用户头像成功", HttpStatus.OK);
    }


}
