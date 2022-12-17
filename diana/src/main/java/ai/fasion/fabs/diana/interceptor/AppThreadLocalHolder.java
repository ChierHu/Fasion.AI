package ai.fasion.fabs.diana.interceptor;


import ai.fasion.fabs.diana.domain.po.AdminUserPO;

/**
 * Function: 缓存用户信息
 *
 * @author miluo
 * Date: 2021/3/5 10:32
 * @since JDK 1.8
 */
public class AppThreadLocalHolder {

    private static final ThreadLocal<AdminUserPO> adminUserInfoPO = new ThreadLocal<>();

    public static String getUserId() {
        AdminUserPO adminUserPO1 = adminUserInfoPO.get();
        if (adminUserPO1 != null) {
            return adminUserPO1.getId();
        }
        return null;
    }

    public static AdminUserPO getUserInfo() {
        return adminUserInfoPO.get();
    }

    public static void setUserInfo(AdminUserPO adminUserPO) {
        AppThreadLocalHolder.adminUserInfoPO.set(adminUserPO);
    }

    public static void clean() {
        adminUserInfoPO.remove();
    }
}
