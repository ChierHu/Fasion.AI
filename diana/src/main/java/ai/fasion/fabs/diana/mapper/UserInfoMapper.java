package ai.fasion.fabs.diana.mapper;

import ai.fasion.fabs.diana.domain.dto.UserInfoDTO;
import ai.fasion.fabs.diana.domain.po.UserInfoPO;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    @Select(" <script>" +
            " SELECT id, phone, nickname, avatar, status, created_at, updated_at, (meta::json->>'application')::json->>'name' AS name," +
            " (meta::json->>'application')::json->>'company' AS company, (meta::json->>'application')::json->>'req_note' AS reqNote" +
            " FROM user_info" +
            " WHERE 1=1" +
            " <if test = \"phone != null and phone != ''\">" +
            " AND phone LIKE concat( '%', #{phone}, '%'  )" +
            " </if>" +
            " <if test = \"uid != null and uid != ''\">" +
            " AND id LIKE concat( '%', #{uid}, '%'  )" +
            " </if>" +
            " </script>")
    List<UserInfoPO> selectAll(String phone, String uid);

    @Select(" SELECT id, phone, nickname, avatar, status, created_at, updated_at FROM user_info WHERE id = #{uid} ")
    UserInfoPO findById(String uid);

    @Update(" <script>" +
            " UPDATE user_info " +
            " <set> " +
            " <if test = \"phone != null and phone != '' \"> " +
            " phone = #{phone}, " +
            " </if> " +
            " <if test = \"nickname != null and nickname != '' \"> " +
            " nickname=#{nickname}, " +
            " </if> " +
            " <if test = \"status != null and status != '' \"> " +
            " status = #{status}, " +
            " </if> " +
            " <if test = \"meta != null and meta != '' \"> " +
            " meta = #{meta}::jsonb, " +
            " </if> " +
            " </set> " +
            " WHERE " +
            " id=#{uid} " +
            " </script> ")
    int updateUserInfo(UserInfoDTO userInfoDTO);
}
