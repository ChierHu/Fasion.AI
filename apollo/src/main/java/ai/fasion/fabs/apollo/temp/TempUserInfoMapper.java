package ai.fasion.fabs.apollo.temp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TempUserInfoMapper {

    /**
     * Update old user id based on new user id
     *
     * @param oldUserId
     * @param newUserId
     * @return
     */
    @Update(" UPDATE admin_oplog SET uid = #{newUserId} WHERE uid = #{oldUserId};" +
            " UPDATE api_app SET uid = #{newUserId} WHERE uid = #{oldUserId};" +
            " UPDATE api_apply SET uid = #{newUserId} WHERE uid = #{oldUserId};" +
            " UPDATE asset SET owner_id = #{newUserId} WHERE owner_id = #{oldUserId};" +
            " UPDATE payment_record SET uid = #{newUserId} WHERE uid = #{oldUserId};" +
            " UPDATE point_balance SET uid = #{newUserId} WHERE uid = #{oldUserId};" +
            " UPDATE point_record SET uid = #{newUserId} WHERE uid = #{oldUserId};" +
            " UPDATE task SET owner_id = #{newUserId} WHERE  owner_id = #{oldUserId};" +
            " UPDATE user_info SET id = #{newUserId} WHERE id = #{oldUserId};")
    void updateUserId(String oldUserId, String newUserId);


    /**
     * query all user ids containing letters
     *
     * @return
     */
    @Select(" select id from user_info where id ~'([a-zA-Z]+)' ORDER BY created_at")
    List<String> listUserId();
}
