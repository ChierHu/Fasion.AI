package ai.fasion.fabs.diana.service;

import ai.fasion.fabs.diana.domain.dto.AdminUserDTO;
import ai.fasion.fabs.diana.domain.po.AdminUserPO;
import ai.fasion.fabs.diana.domain.vo.AdminUserVO;

public interface AdminUserService {

    /**
     * 登陆
     *
     * @param adminUserDTO
     * @return
     */
    AdminUserVO login(AdminUserDTO adminUserDTO);


    /**
     * 检验用户是否登录
     *
     * @param authentication token
     * @return @true:登陆 @false:未登陆
     */
    Boolean authentication(String authentication);

    /**
     * 登出
     */
    void logout();
}
