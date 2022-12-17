package ai.fasion.fabs.vesta.common;

/**
 * Function: 响应码枚举，参考HTTP状态码的语义
 *
 * @author miluo
 * Date: 2018/9/5 上午10:39
 * @since JDK 1.8
 */
public enum ResultCode {
    //成功
    SUCCESS(2000, "成功"),
    //已获得许可
    YE_VERY(2001, "已获得许可"),
    //暂无数据
    NONE_DATA(2002, "暂无数据"),
    //Uploading
    UPLOADING(2003, "加载中"),
    //失败
    FAIL(4000, "失败"),
    //未认证（签名错误）
    UNAUTHORIZED(4001, "未认证"),
    //未验证
    NOT_AUTH(4002, "未获得授权"),
    //加解密异常
    ENCRYPT_DECRYPT_ERROR(4003, "加解密过程异常"),
    //接口不存在
    NOT_FOUND(4004, "接口不存在"),
    //ip不支持
    IP_NOT_SUPPORT(4005, "ip不支持"),
    //身份证/银行卡识别错误
    IDENTIFY_ERROR(4006, "识别错误"),
    //请求类型不被支持
    UNSUPPORTED_MEDIA_TYPE(4115, "请求类型不被支持"),
    //refresh_token 已失效
    REFRESH_TOKEN_INVALID(4442, "refresh_token 已失效"),
    //refresh_token 失效
    REFRESH_TOKEN_TIMEOUT(4443, "refresh_token失效"),
    //token失效
    TOKEN_TIMEOUT(4444, "token失效"),
    //未获得许可（没有权限）
    NO_AUTH(4445, "未获得授权"),
    //认证造假（上传的假token） 或者token上传错误
    TOKEN_ERROR(4446, "token有误"),
    //参数校验错误
    PARAM_VALID(4447, "参数校验错误"),
    //HTTP消息不可读
    HTTP_MESSAGE_NOT_READABLE(4448, "HTTP参数有误(无参或少传)"),
    //文件上传错误
    FILE_UPLOAD_ERROR(4449, "文件上传错误"),
    //文件类型错误
    FILE_UPLOAD_TYPE_ERROR(4450, "文件类型错误"),
    //暂无权限
    NO_PERMISSION(4451, "暂无权限"),
    //用户信息异常
    USER_INFO_EXCEPTION(4552, "用户信息异常"),
    //读取地块信息错误
    GET_LAND_INFO_ERROR(4553, "读取地块信息错误"),
    // 文件不存在
    FILE_NOT_FOUND(4554, "文件不存在"),
    // 目录不为空
    DIRECTORY_NOT_EMPTY(4555, "目录不为空"),
    // 不是文件
    NOT_FILE(4556, "不是文件"),
    // 不是目录
    NOT_DIRECTORY(4557, "不是目录"),
    // 文件已存在
    FILE_EXISTS(4558, "文件已存在"),
    // 请求参数不完整
    PARAM_NOT_COMPLETE(4559, "请求参数不完整"),
    // 用户注册失败
    USER_REGISTER_FAIL(4560, "用户注册失败"),
    // 用户验证失败
    USER_VERIFY_FAIL(4561, "用户验证失败"),
    // 登出失败
    LOGOUT_FAIL(4562, "登出失败"),
    // 滑块验证失败
    SLIDER_VERIFY_FAIL(4563, "滑块验证失败"),
    //该邮箱已注册,请前往登录!
    MAIL_REGISTERED_TO_LOGIN(4564, "该邮箱已注册,请前往登录"),
    //该账号不存在,请重新输入或进行注册!
    ACCOUNT_NOT_EXIST_RE_ENTER(4565, "该账号不存在,请重新输入或进行注册"),
    //验证码过期,请重新生成
    VERIFY_EXPIRED(4566, "验证码过期,请重新生成"),
    //验证码错误,请重新输入
    VERIFY_FAIL(4567, "验证码错误,请重新输入"),
    //超出访问次数
    NUMBER_VISIT_LIMIT(4568, "超出访问次数"),
    //已达到发布次数限制
    NUMBER_PUBLISH_LIMIT(4568, "已达到发布次数限制"),

    //关注的对象不能是自己本身
    DO_NOT_FOLLOW_ME(4800, "不能关注自己"),
    //关注的用户不存在
    FOLLOW_NOT_EXIST(4801, "关注的内容不存在"),

    NOT_TARGET(4701, "目标不存在"),
    TARGET_REPEAT(4702, "目标已存在"),
    NOT_SHOP(4710, "当前用户未曾拥有过店铺"),
    SHOP_IS_AUDIT(4711, "当前用户的店铺在审核中"),
    NOT_APP_KEY(4721, "appkey为空"),
    INVALID_APP_KEY(4822, "appkey无效"),
    NOT_PHONE(4723, "国家代码或者手机号码为空"),
    PHONE_ERROR(4724, "手机号格式错误"),
    NOT_CAPTCHA(4725, "验证码为空"),
    CAPTCHA_ERROR(4726, "验证码错误"),
    SMS_VERIFY_ERROR(4727, "短信验证错误"),
    PASSWORD_ERROR(4728, "用户密码错误"),
    TIME_RANGE_ERROR(4730, "时间范围错误"),

    //服务器内部错误
    INTERNAL_SERVER_ERROR(5000, "服务器内部错误"),
    //服务器响应异常错误
    RESPONSE_RESULT_ERROR(5005, "服务器响应异常"),
    //没那么智能，分辨不出正相关还是负相关
    NOT_SMART_ENOUGH(6008, "无相关性");

    public int code;
    public String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
