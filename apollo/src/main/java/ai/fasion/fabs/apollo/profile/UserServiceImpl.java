package ai.fasion.fabs.apollo.profile;

import ai.fasion.fabs.apollo.auth.vo.UserInfoVO;
import ai.fasion.fabs.apollo.constant.RedisKeyPrefixConstant;
import ai.fasion.fabs.vesta.domain.dos.UserInfoDO;
import ai.fasion.fabs.vesta.expansion.AuthorizationException;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.apollo.auth.vo.MetaVO;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import ai.fasion.fabs.vesta.utils.MultipartFileToFile;
import ai.fasion.fabs.vesta.utils.RestTemplateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.ks3.dto.CannedAccessControlList;
import com.ksyun.ks3.dto.GetObjectResult;
import com.ksyun.ks3.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Function: 用户管理服务类
 *
 * @author yangzhiyuan Date: 2021-01-21 11:02:23
 * @since JDK 1.8
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final UserInfoMapper userInfoMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyPrefixConstant redisKeyPrefixConstant;

    @Value("${apollo.task.url}")
    private String domainUrl;

    @Value("${apollo.ks3.bucket-name}")
    private String bucketName;

    @Value("${apollo.ks3.bucket-domain}")
    private String bucketDomain;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserInfoMapper userInfoMapper, ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate, RedisKeyPrefixConstant redisKeyPrefixConstant) {
        this.passwordEncoder = passwordEncoder;
        this.userInfoMapper = userInfoMapper;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.redisKeyPrefixConstant = redisKeyPrefixConstant;
    }


    /**
     * 修改用户信息
     *
     * @param userInfoVO
     */
    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        UserInfoDO userInfoDO = new UserInfoDO();
        BeanUtils.copyProperties(userInfoVO, userInfoDO);
        //如果用户传来密码
        if (userInfoDO.getPassword() != null) {
            //需要查询库中是否有密码存在
            String userPassword = userInfoMapper.findPassword(userInfoDO.getId());
            if (userPassword.isEmpty()) {
                //如果密码不存在 更新用户的密码以及其它用户信息
                String updatePassword = passwordEncoder.encode(userInfoDO.getPassword());
                userInfoMapper.updatePassword(updatePassword, userInfoDO.getId());
            } else {
                //如果库中用户密码存在不做任何操作
                log.error("库中用户密码存在");
                throw new FailException("库中用户密码存在");
            }
        }
        //更新用户其它信息
        userInfoMapper.updateUserInfo(userInfoDO);
    }

    /**
     * 获取用户个人信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfoVO detail(String userId) {
        UserInfoDO userInfoDO = userInfoMapper.findOne(userId);
        MetaVO metaVO = null;
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfoDO, userInfoVO);
        try {
            if (!StringUtils.isNullOrEmpty(userInfoDO.getMeta())){
                metaVO = objectMapper.readValue(userInfoDO.getMeta(), MetaVO.class);
                userInfoVO.setMeta(metaVO);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return userInfoVO;
    }

    @Override
    public void updatePassword(String userId, String originalPassword, String newPassword) {
        //1.先根据 用户id查询用户的密码
        //userInfo.getId()
        String userPassword = userInfoMapper.findPassword(userId);
        //2.通过BCrypt对比两个密码是否相同，
        boolean result = passwordEncoder.matches(originalPassword, userPassword);
        if (!result) {
            //3.不相同的话，返回403，原始密码输入错误
            log.error("原始密码输入错误");
            throw new FailException("原始密码输入错误");
        }
        //4.如果相同，将newPassword密码 DCrypt更新到数据库
        String newPD = passwordEncoder.encode(newPassword);
        int count = userInfoMapper.updatePassword(newPD, userId);
        if (count == 0) {
            log.error("修改密码失败");
            throw new FailException("修改密码失败");
        }
    }

    @Override
    public void forgetPassword(String code, String password) {
        //通过code获取手机号
        String phoneKey = getKey(code);
        String phone = (String) redisTemplate.opsForValue().get(phoneKey);
        //验证手机验证码是否正确
        String loginKey = getKey(phone);
        checkMessage(code, loginKey);
        //修改密码
        String newPassword = passwordEncoder.encode(password);
        int count = userInfoMapper.updatePasswordByPhone(newPassword, phone);
        if (count == 0) {
            log.error("修改密码失败");
            throw new FailException("修改密码失败");
        }
    }

    @Override
    public void updateProfileImage(String id, MultipartFile file) {
        File tempFile = MultipartFileToFile.multipartFileToFile(file);
        KsOssUtil.getInstance().putObjectSimple(bucketName, "aaa/"+id, tempFile, CannedAccessControlList.PublicRead);
        String path = "https://"+bucketDomain+"/aaa/"+id;
        int result = userInfoMapper.updateProfileImage(id, path);
        if (result == 0) {
            throw new FailException("更新用户头像失败");
        }
    }

    /**
     * 验证短信验证码
     */
    private void checkMessage(@NotNull String code, @NotNull String phone) {
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
     * 获取redis Key
     */
    private String getKey(String phone) {
        return redisKeyPrefixConstant.phoneCode + ":" + phone;
    }
}
