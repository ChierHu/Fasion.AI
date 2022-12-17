package ai.fasion.fabs.vesta.enums;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/6/1 15:26
 * @since JDK 1.8
 */
public enum UserTypeEnum {
    /**
     * 批量换脸
     */
    USER("user"),

    API_APP("api_app");

    /**
     * 判断字符串名字是否属于枚举类
     *
     * @param name
     * @return
     */
    public static UserTypeEnum getUserTypeEnum(String name) {
        UserTypeEnum userType = null;
        boolean include = false;
        for (UserTypeEnum e : UserTypeEnum.values()) {
            if (e.getName().equals(name)) {
                userType = e;
                break;
            }
        }
        return userType;
    }


    /**
     * 类型中文名称
     */
    private String name;


    UserTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
