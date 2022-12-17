package ai.fasion.fabs.vulcan.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Function: 生产随机数
 *
 * @author yangzhiyuan Date: 2021/1/7 13:36
 * @since JDK 1.8
 */
public class RandomUtils {

    private static List<Character> pondList;

    private static List<Character> pondNumberList;

    static {
        // 去掉数字 0 1 去掉字母 I L O
        Character[] pond =
                new Character[]{
                        '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'E', 'F', 'G', 'H', 'J', 'K', 'M',
                        'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
                };
        pondList = Arrays.asList(pond);

        Character[] pondNumber = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        pondNumberList = Arrays.asList(pondNumber);
    }

    /**
     * 获取n位随机数
     *
     * @param num
     * @return
     */
    public static String generateRandom(Integer num) {
        StringBuilder sb = new StringBuilder("");
        Random r = new Random();
        int count = 0;
        Integer i;
        while (count < num) {
            i = Math.abs(r.nextInt(pondList.size() - 1));
            if (i >= 0 && i < pondList.size()) {
                sb.append(pondList.get(i));
                count++;
            }
        }
        return sb.toString();
    }

    /**
     * 获取n位随机数
     *
     * @param num
     * @return
     */
    public static String generateNumberRandom(Integer num) {
        StringBuilder sb = new StringBuilder("");
        Random r = new Random();
        int count = 0;
        Integer i;
        while (count < num) {
            i = Math.abs(r.nextInt(pondNumberList.size() - 1));
            if (i >= 0 && i < pondNumberList.size()) {
                sb.append(pondNumberList.get(i));
                count++;
            }
        }
        return sb.toString();
    }
}
