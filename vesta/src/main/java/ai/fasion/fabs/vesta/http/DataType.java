package ai.fasion.fabs.vesta.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Function:服务端响应的数据类型
 *
 * @author miluo Date: 2018/9/9 下午6:06
 * @since JDK 1.8
 */
public class DataType {
  /** 返回数据为String */
  public static final int STRING = 1;
  /** 返回数据为xml类型 */
  public static final int XML = 2;
  /** 返回数据为json对象 */
  public static final int JSON_OBJECT = 3;
  /** 返回数据为json数组 */
  public static final int JSON_ARRAY = 4;

  /** 自定义一个播放器状态注解 */
  @Retention(RetentionPolicy.SOURCE)
  public @interface Type {}
}
