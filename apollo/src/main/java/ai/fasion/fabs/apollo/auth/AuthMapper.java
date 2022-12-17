package ai.fasion.fabs.apollo.auth;

import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.Date;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/25 16:25
 * @since JDK 1.8
 */
@Mapper
public interface  AuthMapper {


    String JSON_TYPE_HANDLER = "ai.fasion.fabs.vesta.model.JSONTypeHandlerPg";

    @Select("SELECT id,  phone, nickname, avatar, status FROM user_info WHERE id=#{uid}")
    UserInfoDO getUserInfoByUid(String uid);

    /**
     * 根据电话号码查找用户信息
     *
     * @param phone
     * @return
     */
    @Select("SELECT id, phone, password, email, email_status, password, nickname, avatar, status FROM user_info WHERE phone = #{phone} ")
    UserInfoDO searchUserInfoByPhone(String phone);

    /**
     * 保存用户信息
     *
     * @param userInfoDO
     * @return
     */
    @Insert(" INSERT INTO user_info ( id, phone, password, nickname, avatar, status ) " +
            " VALUES (#{id}, #{phone}, #{password}, #{nickname}, #{avatar}, #{status} ) ")
    int saveUserInfo(UserInfoDO userInfoDO);

    /**
     * 根据用户id更新最后登陆时间
     *
     * @param uid
     * @param now
     */
    @Update("UPDATE user_extra SET last_login_at = #{now} WHERE id=#{uid} ")
    void updateLastLoginByUid(String uid, Date now);

    /**
     * 更新用户申请试用信息
     * @param userInfoDO
     * @return
     */
    @Update("UPDATE user_info SET nickname = #{nickname}, status = #{status}, meta = #{meta}::jsonb WHERE id = #{id}")
    int updateUserInfoById(UserInfoDO userInfoDO);

    /**
     * 获取最新的用户id
     *
     * @return 最新的用户id
     */
    @Select("SELECT id FROM user_info WHERE id~ '^[0-9]+?$' ORDER BY created_at DESC limit 1")
    Integer getNewestUserId();
}
