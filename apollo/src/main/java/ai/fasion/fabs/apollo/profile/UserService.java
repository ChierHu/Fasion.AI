package ai.fasion.fabs.apollo.profile;

import ai.fasion.fabs.apollo.auth.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Function: 账号管理service
 *
 * @author yangzhiyuan Date: 2021-01-21 10:52:39
 * @since JDK 1.8
 */
public interface UserService {


    /**
     * 修改用户信息
     *
     * @param userInfoVO
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 获取用户个人信息
     *
     * @param userId
     * @return
     */
    UserInfoVO detail(String userId);

    /**
     * 原密码验证后修改密码
     */
    void updatePassword(String userId, String originalPassword, String newPassword );



    /**
     * 忘记密码，通过短信验证修改密码
     */
    void forgetPassword(String code, String password);

    /**
     * 修改用户头像
     * @param id
     * @param file
     * @return
     */
    void updateProfileImage(String id, MultipartFile file);


}
