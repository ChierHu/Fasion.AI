package ai.fasion.fabs.vesta.enums;

/**
 * Function:缓存枚举类
 *
 * @author miluo Date: 2020/4/24 1:03 下午
 * @since JDK 1.8
 */
public enum CacheEnum {

  /** 接口限流 */
  REQUEST_LIMIT("req_limit", "接口限流key"),

  /** 店铺发布次数 */
  SHOP_ACTIVITY_PUBLISH_LIMIT("shop_activity_publish_limit", "店铺发布限制");

  /** 缓存的key */
  private String key;

  /** 缓存说明 */
  private String description;

  CacheEnum(String key, String description) {
    this.key = key;
    this.description = description;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
