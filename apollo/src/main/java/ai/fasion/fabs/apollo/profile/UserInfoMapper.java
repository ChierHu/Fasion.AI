package ai.fasion.fabs.apollo.profile;

import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Function: 用户管理mapper
 *
 * @author yangzhiyuan Date: 2021-01-21 15:42:57
 * @since JDK 1.8
 */
@Mapper
public interface UserInfoMapper {

    @Select("SELECT id, phone, nickname, status, avatar, meta  FROM user_info WHERE id=#{userId}")
    UserInfoDO findOne(String userId);

    @Update(" <script>" +
            " UPDATE user_info " +
            " <set> " +
            " <if test = \"nickname != null and nickname != '' \"> " +
            " nickname=#{nickname}, " +
            " </if> " +
            " <if test = \"avatar != null and avatar != '' \"> " +
            " avatar=#{avatar}, " +
            " </if> " +
            " </set> " +
            " WHERE " +
            " id=#{id} " +
            " </script> ")
    void updateUserInfo(UserInfoDO userInfoDO);

    @Select("SELECT password  FROM user_info WHERE id=#{userId}")
    String findPassword(String userId);

    /**
     * 修改用户密码
     */
    @Update(" UPDATE user_info SET password = #{password} WHERE id = #{id} ")
    int updatePassword(String password, String id);

    /**
     * 通过手机号修改用户密码
     */
    @Update(" UPDATE user_info SET password = #{password} WHERE phone = #{phone} ")
    int updatePasswordByPhone(String password, String phone);

    @Update(" UPDATE user_info SET avatar = #{profileAddress} WHERE id = #{id} ")
    int updateProfileImage(String id, String profileAddress);


}
