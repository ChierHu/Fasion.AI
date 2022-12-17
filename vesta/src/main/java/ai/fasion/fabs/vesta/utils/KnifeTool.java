package ai.fasion.fabs.vesta.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Function: 工具箱
 *
 * @author miluo
 * Date: 2021/7/8 15:31
 * @since JDK 1.8
 */
public class KnifeTool {

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
