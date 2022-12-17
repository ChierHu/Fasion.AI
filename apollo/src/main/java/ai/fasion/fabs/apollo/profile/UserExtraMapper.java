package ai.fasion.fabs.apollo.profile;

import ai.fasion.fabs.apollo.auth.po.UserExtraPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserExtraMapper {

    @Insert("INSERT INTO user_extra (id) VALUES (#{id})")
    int saveUserExtra(UserExtraPO userExtraPO);
}
