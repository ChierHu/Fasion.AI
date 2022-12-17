package ai.fasion.fabs.apollo.auth;

import ai.fasion.fabs.apollo.auth.vo.RetLoginUserInfoVO;
import ai.fasion.fabs.apollo.auth.vo.UserCodeVO;
import ai.fasion.fabs.apollo.auth.vo.LoginUserLoginVO;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;

import java.util.Map;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/25 16:21
 * @since JDK 1.8
 */
public interface AuthService {

    /**
     * 校验请求是否存在token
     *
     * @param authentication
     */
    UserInfoDO authentication(String authentication);

    /**
     * 判断用户的状态是否为activity
     */
    void judgeUserStatus(UserInfoDO userInfo);


    /**
     * 校验用户是否登录
     *
     * @param authentication
     * @return
     */
    Boolean commonAuthentication(String authentication);


    /**
     * 发送验证码
     *
     * @param userCodeVO
     */
    void sendVerificationCode(UserCodeVO userCodeVO);

    /**
     * 测试环境发送验证码
     *
     * @param userCodeVO
     */
    void developSendVerificationCode(UserCodeVO userCodeVO);

    /**
     * 登录
     *
     * @param loginUserLoginVO
     * @return
     */
    RetLoginUserInfoVO login(LoginUserLoginVO loginUserLoginVO) throws Exception;

    /**
     * 登出
     */
    void logout();

    /**
     * 更新用户申请试用信息
     *
     * @param map
     * @param id
     * @return
     */
    int updateUserInfoById(Map<String, Object> map, String id);


    /**
     * 获取数据库中最新的用户id
     *
     * @return 最新的用户id
     */
    Integer getNewestUserId();

    /**
     * 生成一个最新的UserId
     *
     * @return
     */
    Integer createNewUserId();
}
