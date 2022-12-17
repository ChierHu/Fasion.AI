package ai.fasion.fabs.vesta.domain;

/**
 * Function:用于返回空对象
 *
 * @author miluo
 * Date: 2021/5/26 20:39
 * @since JDK 1.8
 */
public class ResponseEmptyEntity {
    /**
     * 私有构造器
     */
    private ResponseEmptyEntity() {
    }

    private static final ResponseEmptyEntity INSTANCE = new ResponseEmptyEntity();

    public static ResponseEmptyEntity getInstance() {
        return INSTANCE;
    }
}
