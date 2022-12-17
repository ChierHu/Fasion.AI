package ai.fasion.fabs.apollo.config;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * Function: 敏感词过滤
 *
 * @author miluo
 * Date: 2021/3/3 11:50
 * @since JDK 1.8
 */
@Mapper
public interface SensitiveWordMapper {
    /**
     * 查询敏感词信息
     *
     * @return
     */
    @Select("SELECT id, word, category, create_time, update_time FROM sensitive_word ")
    List<SensitiveWordPO> searchSensitiveWordInfo();

    /**
     * 查询敏感词
     *
     * @return
     */
    @Select("SELECT word FROM sensitive_word ")
    Set<String> searchSensitiveWord();
}
