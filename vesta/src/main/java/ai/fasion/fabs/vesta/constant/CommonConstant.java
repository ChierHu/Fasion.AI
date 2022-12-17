package ai.fasion.fabs.vesta.constant;

import java.util.Arrays;

public class CommonConstant {

    /**
     * 启用状态
     */
    public static final Integer STATUS_ENABLE = 1;

    /**
     * 未启用状态
     */
    public static final Integer STATUS_NOT_ENABLED = 0;

    public static final Integer[] COMMON_STATUS = {STATUS_ENABLE, STATUS_NOT_ENABLED};

    public static boolean checkStatus(Integer status) {
        return Arrays.asList(COMMON_STATUS).contains(status);
    }
}
