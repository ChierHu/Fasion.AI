package ai.fasion.fabs.apollo.auth;

import ai.fasion.fabs.apollo.auth.vo.*;
import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.apollo.constant.RedisKeyPrefixConstant;
import ai.fasion.fabs.apollo.auth.po.UserExtraPO;
import ai.fasion.fabs.apollo.profile.UserExtraMapper;
import ai.fasion.fabs.vesta.enums.UserInfoEnum;
import ai.fasion.fabs.vesta.utils.RandomUtils;
import ai.fasion.fabs.vesta.constant.Consts;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import ai.fasion.fabs.vesta.expansion.AuthorizationException;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.model.SendSmsResult;
import ai.fasion.fabs.vesta.service.SmsService;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import ai.fasion.fabs.vesta.service.context.ThreadLocalHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/25 16:22
 * @since JDK 1.8
 */
@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    /**
     * 设置成功状态码
     */
    private static final int ACCESS_SUCCESS = 200;


    /**
     * userid初始值
     */
    @Value("${apollo.uid.seed}")
    private Integer seed;

    /**
     * 步长
     */
    @Value("${apollo.uid.step}")
    private Integer step;

    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthMapper authMapper;
    private final UserExtraMapper userExtraMapper;
    private final RedisKeyPrefixConstant redisKeyPrefixConstant;
    private final KsOssConstant ksOssConstant;
    private final ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public AuthServiceImpl(RedisTemplate<String, Object> redisTemplate, AuthMapper authMapper, UserExtraMapper userExtraMapper, RedisKeyPrefixConstant redisKeyPrefixConstant, KsOssConstant ksOssConstant, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.authMapper = authMapper;
        this.userExtraMapper = userExtraMapper;
        this.redisKeyPrefixConstant = redisKeyPrefixConstant;
        this.ksOssConstant = ksOssConstant;
        this.objectMapper = objectMapper;
    }

    /**
     * 如果将PasswordEncoder也放入构造器中进行装配，会出现循环依赖问题
     * 原因：
     * 1. 在springboot启动时，PasswordEncoder需要在MyWebMvcConfig中定义bean，MyWebMvcConfig实现Springboot的WebMvcConfigurer类
     * 2. MyWebMvcConfig中引用了AuthServiceImpl类中的方法，AuthServiceImpl引用了MyWebMvcConfig类中的PasswordEncoder，形成循环引用
     * 3. Springboot构造器注入默认是不会智能判断什么时候应该注入
     * 解决：
     * 1. 在字段上使用@Autowired注解，让Spring决定在合适的时机注入。(idea会报warning，因此不推荐)
     * 2. 用基于setter方法的依赖注射取代基于构造函数的依赖注入来解决循环依赖。
     *
     * @param passwordEncoder 加解密接口
     */
    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 校验用户是否登录
     *
     * @param authentication
     */
    @Override
    public UserInfoDO authentication(String authentication) {
        // 请求头中没有authentication
        if (StringUtils.isBlank(authentication) || "null".equals(authentication)) {
            throw new AuthorizationException("暂未登录或token已经过期");
        }
        // redis中没有key
        String authKey = getAuthKey(authentication);
        log.info("[校验用户是否登陆] redis->{}", authKey);
        UserInfoDO userInfoDO = (UserInfoDO) redisTemplate.opsForValue().get(authKey);
//        if (userInfoDO == null) {
//            // redis中value，不在数据库中
//            userInfoDO = authMapper.getUserInfoByUid(userInfoDO.getId());
//            if (userInfoDO == null) {
//                log.error("暂未登录或token已经过期");
//                throw new AuthorizationException("暂未登录或token已经过期");
//            }
//        }

        if (userInfoDO == null) {
            log.error("暂未登录或token已经过期");
            throw new AuthorizationException("暂未登录或token已经过期");
        }

        // 校验通过，将userInfo放入AppThreadLocalHolder
        AppThreadLocalHolder.setUserInfo(userInfoDO);
        // 访问服务后，顺延缓存30天
        redisTemplate.boundValueOps(authentication).expire(30, TimeUnit.DAYS);
        return userInfoDO;
    }

    @Override
    public void judgeUserStatus(UserInfoDO userInfo) {
        UserInfoDO userInfoDO = authMapper.getUserInfoByUid(userInfo.getId());
        if (!userInfoDO.getStatus().equals(UserInfoEnum.Status.ACTIVE)) {
            throw new AuthorizationException("当前用户状态未激活！");
        }
    }

    @Override
    public Boolean commonAuthentication(String authentication) {
        // 请求头中没有authentication
        if (StringUtils.isBlank(authentication)) {
            return false;
        }
        // redis中没有key
        String authKey = getAuthKey(authentication);
        log.info("[校验用户是否登陆] redis->{}", authKey);
        UserInfoDO userInfoDO = (UserInfoDO) redisTemplate.opsForValue().get(authKey);
        if (userInfoDO == null) {
            return false;
        }

        //如果全局的AppThreadLocalHolder存在就直接返回true
        UserInfoDO userInfo = AppThreadLocalHolder.getUserInfo();
        if (userInfo != null) {
            return true;
        }

        // redis中value，不在数据库中
        UserInfoDO umsUserInfoDO = authMapper.getUserInfoByUid(userInfoDO.getId());
        if (umsUserInfoDO == null) {
            return false;
        }
        // 校验通过，将userInfo放入AppThreadLocalHolder
        AppThreadLocalHolder.setUserInfo(userInfoDO);
        // 访问服务后，顺延缓存30天
        redisTemplate.boundValueOps(authentication).expire(30, TimeUnit.DAYS);
        return true;
    }

    /**
     * 发送验证码
     */
    @Override
    public void sendVerificationCode(UserCodeVO userCodeVO) {
        log.info("【短信验证码】要接收验证码的手机号为：{}", userCodeVO.getPhone());
        //登陆码
        String loginCode = RandomUtils.generateNumberRandom(4);
        //登陆模板
        Map<String, String> tplParams = Collections.singletonMap("number", loginCode);
        //第三方短信验证码服务
        SmsService smsService = new SmsService(Consts.AK, Consts.SK, Consts.SIGN_NAME);
        SendSmsResult sendSmsResult = smsService.sendSms(userCodeVO.getPhone(), Consts.TPL_ID, tplParams, "", "");
        //如果状态为200，则表示成功
        if (ACCESS_SUCCESS == sendSmsResult.getCode()) {
            log.info("【短信验证码】发送成功，手机号为：{}", userCodeVO.getPhone());
            String loginKey = getLoginKey(userCodeVO.getPhone());
            String phoneKey = getLoginKey(loginCode);
            // 存放在redis里
            redisTemplate.opsForValue().set(loginKey, loginCode, 5, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(phoneKey, userCodeVO.getPhone(), 5, TimeUnit.MINUTES);
        } else {
            log.error("【短信验证码】发送失败，状态不为200，手机号为：{}", userCodeVO.getPhone());
        }
    }

    @Override
    public void developSendVerificationCode(UserCodeVO userCodeVO) {
        log.info("【测试环境-短信验证码】要接收验证码的手机号为：{}", userCodeVO.getPhone());
        //登陆码
        String loginCode = userCodeVO.getPhone().substring(userCodeVO.getPhone().length() - 4);
        log.info("【测试环境-短信验证码】发送成功，手机号为：{}", userCodeVO.getPhone());
        String loginKey = getLoginKey(userCodeVO.getPhone());
        String phoneKey = getLoginKey(loginCode);
        // 存放在redis里
        redisTemplate.opsForValue().set(loginKey, loginCode, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(phoneKey, userCodeVO.getPhone(), 5, TimeUnit.MINUTES);
    }


    /**
     * 登录
     *
     * @param loginUserLoginVO
     * @return
     */
    @Override
    public RetLoginUserInfoVO login(LoginUserLoginVO loginUserLoginVO) {
        RetLoginUserInfoVO retLoginUser;
        log.info("phone number is ：[{}]", loginUserLoginVO.getPhone());
        String loginKey = getLoginKey(loginUserLoginVO.getPhone());
        // 验证手机验证码是否正确
        checkMessage(loginUserLoginVO.getCode(), loginKey);
        // 根据手机号判断登陆
        UserInfoDO userInfoDO = authMapper.searchUserInfoByPhone(loginUserLoginVO.getPhone());
        // 账号不存在，第一次登陆设置初始化信息
        if (userInfoDO == null) {
            log.info("【登陆】第一次登陆，手机号为：{}", loginUserLoginVO.getPhone());
            // 第一次注册，插入记录到user
            UserInfoDO userInfo = saveUser(loginUserLoginVO);
            // todo 扩展表没有什么用了，需要等待合适时机删除
            //插入用户的用户的扩展表
            // saveUserExtra(userInfo);
            UserInfoVO userInfoVO = putCache(userInfo);
            retLoginUser = new RetLoginUserInfoVO();
            retLoginUser.setAvatar(userInfoVO.getAvatar());
            retLoginUser.setUid(userInfoVO.getId());
            retLoginUser.setNickname(userInfoVO.getNickname());
            retLoginUser.setPhone(userInfoVO.getPhone());
            retLoginUser.setToken(userInfoVO.getToken());
            retLoginUser.setStatus(userInfoVO.getStatus());
            return retLoginUser;
        }

        //判断账号是否禁用
        if (UserInfoEnum.Status.BANNED.equals(userInfoDO.getStatus())) {
            log.error("【登陆】账号已被封停，手机号为：{}", loginUserLoginVO.getPhone());
            throw new AuthorizationException("账号已被封停");
        }

        //如果用户不是第一次登陆需要验证密码
        if (null != loginUserLoginVO.getPassword()) {
            log.info("password compare： {}", loginUserLoginVO.getPhone());
            boolean result = passwordEncoder.matches(loginUserLoginVO.getPassword(), userInfoDO.getPassword());
            if (null != userInfoDO.getPassword() && !result) {
                log.error("登陆失败,密码错误");
                throw new AuthorizationException("登陆失败,密码错误");
            }
        }

        UserInfoVO userInfoVO = putCache(userInfoDO);
        retLoginUser = new RetLoginUserInfoVO();
        retLoginUser.setUid(userInfoVO.getId());
        retLoginUser.setAvatar(userInfoVO.getAvatar());
        retLoginUser.setNickname(userInfoVO.getNickname());
        retLoginUser.setPhone(userInfoVO.getPhone());
        retLoginUser.setToken(userInfoVO.getToken());
        retLoginUser.setStatus(userInfoVO.getStatus());
        return retLoginUser;
    }

    @Override
    public void logout() {
        String authentication = ThreadLocalHolder.getRequest().getHeader("Authorization");
        if (StringUtils.isBlank(authentication)) {
            log.error("暂未登录或token已经过期");
            throw new AuthorizationException("暂未登录或token已经过期");
        }
        //  清除redis key
        String authKey = getAuthKey(authentication);
        redisTemplate.delete(authKey);
    }

    @Override
    public Integer getNewestUserId() {
        return authMapper.getNewestUserId();
    }

    @Override
    public Integer createNewUserId() {
        //1.获取库里面最新的userId
        Integer currentDataBaseNewestUserId = getNewestUserId();
        //2. 判断newestUserId是否存在
        if (null == currentDataBaseNewestUserId) {
            //不存在时，证明数据库里是空的，那么需要获取配置文件中的初始值用于
            currentDataBaseNewestUserId = seed;
        }
        //3. 初始化随机实例
        Random random = new Random();

        int newGenerationUserId;
        //4. 从配置文件中获取步长信息
        //5. 生成一个新的userId
        do {
            //8. 如果步长为0，那么直接返回500错误，证明已经不能再继续增长
            if (step <= 0) {
                log.error("无法创建新用户id");
                throw new RuntimeException("无法创建新用户id");
            }
            newGenerationUserId = random.nextInt(step) + (currentDataBaseNewestUserId + 1);
            //6. 判断新生成的userId是否超过int最大上限
            if (newGenerationUserId < 0) {
                //7. 如果超过最大上限，获取数据库中最新userId和最大上限之间的差值，作为新的步长
                step = Integer.MAX_VALUE - (currentDataBaseNewestUserId + step);
            }
        } while (newGenerationUserId < 0);
        //9. 如果不长大于0，那么直接返回新userId
        return newGenerationUserId;
    }

    /**
     * 登录，将token放入缓存，并返回给前端
     *
     * @param userInfoDO
     * @return
     */
    private UserInfoVO putCache(UserInfoDO userInfoDO) {
        String token = UUID.randomUUID().toString();
        String authKey = getAuthKey(token);
        // 放入redis中，缓存30天
        redisTemplate.opsForValue().set(authKey, userInfoDO, 30, TimeUnit.DAYS);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfoDO, userInfoVO);
        userInfoVO.setToken(token);
        //设置登录时间
        authMapper.updateLastLoginByUid(userInfoDO.getId(), new Date());
        return userInfoVO;
    }

    /**
     * 验证短信验证码
     *
     * @param code
     * @return
     */
    private void checkMessage(String code, String phone) {
        // 短信方式校验
        if (StringUtils.isBlank(code)) {
            throw new AuthorizationException("短信验证码不存在");
        }
        // key为手机号
        String redisCode = (String) redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)) {
            log.error("验证码不正确");
            throw new AuthorizationException("验证码不正确");
        }
        // 验证码对比正确，删除redis
        redisTemplate.delete(phone);
    }

    /**
     * 获取redis AuthKey
     *
     * @param token
     * @return
     */
    private String getAuthKey(String token) {
        return redisKeyPrefixConstant.authCode + ":" + token;
    }

    /**
     * 获取redis LoginKey
     *
     * @param phone
     * @return
     */
    private String getLoginKey(String phone) {
        return redisKeyPrefixConstant.phoneCode + ":" + phone;
    }


    /**
     * 保存user
     *
     * @return
     */
    UserInfoDO saveUser(LoginUserLoginVO loginUserLoginVO) {
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setId(createNewUserId().toString());
        userInfo.setPhone(loginUserLoginVO.getPhone());
        //用户密码 对密码进行加密
        if (null != loginUserLoginVO.getPassword()) {
            String password = passwordEncoder.encode(loginUserLoginVO.getPassword());
            userInfo.setPassword(password);
        }

        userInfo.setStatus(UserInfoEnum.Status.Created.getLabel());
        userInfo.setNickname("新用户_" + RandomUtils.generateRandom(3));
        userInfo.setAvatar("https://" + ksOssConstant.getBucketDomain() + "/" + ksOssConstant.getDefaultAvatar());
        // 保存用户登陆信息
        int result = authMapper.saveUserInfo(userInfo);
        if (result == 0) {
            log.error("保存失败");
            throw new FailException("保存失败");
        }
        return userInfo;
    }

    UserExtraPO saveUserExtra(UserInfoDO userInfo) {
        UserExtraPO userExtraPO = new UserExtraPO();
        userExtraPO.setId(userInfo.getId());
        int result = userExtraMapper.saveUserExtra(userExtraPO);
        if (result == 0) {
            log.error("保存失败");
            throw new FailException("保存失败");
        }
        return userExtraPO;
    }

    @Override
    public int updateUserInfoById(Map<String, Object> map, String id) {
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setNickname(map.get("name").toString());
        userInfoVO.setStatus(UserInfoEnum.Status.Pending.getLabel());
        userInfoVO.setId(id);

        Map<String, Object> applicationMap = new HashMap<>();
        //把时间添加进map
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("timestamp", sdf.format(new Date()));
        applicationMap.put("application", map);
        String metaJson = null;
        try {
            //map转换成Json
            metaJson = objectMapper.writeValueAsString(applicationMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        BeanUtils.copyProperties(userInfoVO, userInfoDO);
        userInfoDO.setMeta(metaJson);
        int result = authMapper.updateUserInfoById(userInfoDO);
        if (result == 0) {
            log.error("更新失败");
            throw new FailException("更新失败");
        }
        return result;
    }
}
