package ai.fasion.fabs.diana.mapper;

import ai.fasion.fabs.diana.domain.po.AdminUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminUserMapper {

    /**
     * 根据用户名密码检查查询用户信息
     *
     * @param username
     * @param password
     * @return
     */
    @Select("SELECT id, username, nickname, status, created_at, updated_at FROM admin_user WHERE username = #{username} AND password = #{password} ")
    AdminUserPO login(String username, String password);

    /**
     * 根据用户id查询用户信息
     *
     * @param id
     * @return
     */
    @Select("SELECT id, username, nickname, status, created_at, updated_at FROM admin_user WHERE id = #{id} ")
    AdminUserPO findOneById(String id);

}
