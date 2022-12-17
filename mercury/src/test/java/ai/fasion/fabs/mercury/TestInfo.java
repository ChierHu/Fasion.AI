package ai.fasion.fabs.mercury;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/9/14 15:44
 * @since JDK 1.8
 */
public class TestInfo {
    static int cnt = 6;

    static {
        cnt += 9;
    }

    public static void main(String[] args) {
        System.out.println(cnt);
    }

    static {
        cnt /= 3;
    }
}
