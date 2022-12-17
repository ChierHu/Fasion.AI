package ai.fasion.fabs.vesta.expansion;

/**
 * Function: 加密请求超时
 *
 * @author miluo
 * Date: 2021/1/22 18:22
 * @since JDK 1.8
 */
public class EncryptRequestException extends FailException {

    public EncryptRequestException(String msg) {
        super(msg);
    }
}