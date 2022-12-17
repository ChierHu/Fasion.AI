package ai.fasion.fabs.apollo.assets.pojo;

import java.util.List;

/**
 * Function: 金山云规则
 *
 * @author miluo
 * Date: 2021/5/26 15:35
 * @since JDK 1.8
 */
public class Policy {

    /**
     * 过期时间
     */
    public String expiration;

    /**
     * 条件
     */
    public List<List<String>> conditions;

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }



    public void setConditions(List<List<String>> conditions) {
        this.conditions = conditions;
    }
}
