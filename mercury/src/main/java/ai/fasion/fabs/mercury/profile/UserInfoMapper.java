package ai.fasion.fabs.mercury.profile;

import ai.fasion.fabs.mercury.payment.MetaInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Function: 用户管理mapper
 *
 * @author yangzhiyuan Date: 2021-01-21 15:42:57
 * @since JDK 1.8
 */
@Mapper
public interface UserInfoMapper {

    @Select("SELECT  phone, email, nickname, status, avatar  FROM user_info WHERE id=#{uid}")
    MetaInfo.UserSnapshot findOne(String uid);


}
