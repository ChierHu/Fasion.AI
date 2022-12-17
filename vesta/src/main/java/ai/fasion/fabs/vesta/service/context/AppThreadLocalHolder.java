package ai.fasion.fabs.vesta.service.context;

import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;

/**
 * Function: 缓存用户信息
 *
 * @author miluo
 * Date: 2021/3/5 10:32
 * @since JDK 1.8
 */
public class AppThreadLocalHolder {

    private static final ThreadLocal<UserInfoDO> userInfoDO = new ThreadLocal<>();

    /**
     * 获取当前用户id
     *
     * @return
     */
    public static String getUserId() {
        UserInfoDO userInfoDO = AppThreadLocalHolder.userInfoDO.get();
        if (userInfoDO != null) {
            return userInfoDO.getId();
        }
        return null;
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static UserInfoDO getUserInfo() {
        return userInfoDO.get();
    }

    public static void setUserInfo(UserInfoDO userInfoDO) {
        AppThreadLocalHolder.userInfoDO.set(userInfoDO);
    }

    public static void clean() {
        userInfoDO.remove();
    }
}
