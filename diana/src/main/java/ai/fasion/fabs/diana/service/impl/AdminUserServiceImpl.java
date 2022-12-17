package ai.fasion.fabs.diana.service.impl;

import ai.fasion.fabs.diana.config.RedisKeyPrefixConfig;
import ai.fasion.fabs.diana.constant.KsOssConstant;
import ai.fasion.fabs.diana.domain.dto.AdminUserDTO;
import ai.fasion.fabs.diana.domain.po.AdminUserPO;
import ai.fasion.fabs.diana.domain.vo.AdminUserVO;
import ai.fasion.fabs.diana.interceptor.AppThreadLocalHolder;
import ai.fasion.fabs.diana.mapper.AdminUserMapper;
import ai.fasion.fabs.diana.service.AdminUserService;
import ai.fasion.fabs.vesta.enums.AdminUserEnum;
import ai.fasion.fabs.vesta.expansion.AuthorizationException;
import ai.fasion.fabs.vesta.service.context.ThreadLocalHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    private static final Logger log = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    private final AdminUserMapper adminUserMapper;

    private final RedisKeyPrefixConfig redisKeyPrefixConfig;

    private final RedisTemplate<String, Object> redisTemplate;

    private final KsOssConstant ksOssConstant;

    public AdminUserServiceImpl(AdminUserMapper adminUserMapper, RedisKeyPrefixConfig redisKeyPrefixConfig, RedisTemplate<String, Object> redisTemplate, KsOssConstant ksOssConstant) {
        this.adminUserMapper = adminUserMapper;
        this.redisKeyPrefixConfig = redisKeyPrefixConfig;
        this.redisTemplate = redisTemplate;
        this.ksOssConstant = ksOssConstant;
    }


    @Override
    public AdminUserVO login(AdminUserDTO adminUserDTO) {
        //进行用户名，密码验证
        AdminUserPO adminUserPO = adminUserMapper.login(adminUserDTO.getUsername(), adminUserDTO.getPassword());
        //如果数据库没有这个用户进行注册功能
        if (null != adminUserPO) {
            if (!adminUserPO.getStatus().equals(AdminUserEnum.Status.Enable.getLabel())) {
                throw new AuthorizationException("该用户处于禁用状态无法登录系统");
            }
            //登陆成功添加到token
            String token = UUID.randomUUID().toString();
            String authKey = getAuthKey(token);
            //放入redis中 缓存30天
            redisTemplate.opsForValue().set(authKey, adminUserPO, 30, TimeUnit.DAYS);
            AdminUserVO adminUserVO = new AdminUserVO();
            BeanUtils.copyProperties(adminUserPO, adminUserVO);
            // 默认一个用户后台管理头像
            adminUserVO.setAvatar("http://" + ksOssConstant.getBucketDomain() + "/" + ksOssConstant.getDefaultAvatar());
            adminUserVO.setToken(token);
            adminUserVO.setUid(adminUserPO.getId());
            adminUserVO.setStatus(adminUserPO.getStatus());
            return adminUserVO;
        }
        return null;
    }

    @Override
    public Boolean authentication(String authentication) {
        //请求头中没有authentication
        if (StringUtils.isBlank(authentication)|| "null".equals(authentication)) {
            throw new AuthorizationException("暂未登录或token已经过期");
        }
        //redis中没有key
        String authKey = getAuthKey(authentication);
        AdminUserPO adminUser = (AdminUserPO) redisTemplate.opsForValue().get(authKey);
        if (null == adminUser) {
            throw new AuthorizationException("暂未登录或token已经过期");
        }
        //redis中value，不在数据库中
        adminUser = adminUserMapper.findOneById(adminUser.getId());
        if (null == adminUser) {
            throw new AuthorizationException("暂未登录或token已经过期");
        }
        //验证通过将用户信息放入AppThreadLocalHolder
        AppThreadLocalHolder.setUserInfo(adminUser);
        //访问服务后，顺延缓存30天
        redisTemplate.boundGeoOps(authentication).expire(30, TimeUnit.DAYS);
        log.info("登录成功，token顺延30天");
        return true;
    }

    @Override
    public void logout() {
        String authentication = ThreadLocalHolder.getRequest().getHeader("Authorization");
        if (StringUtils.isBlank(authentication)) {
            throw new AuthorizationException("暂未登录或token已经过期");
        }
        //清除redis key
        String authKey = getAuthKey(authentication);
        redisTemplate.delete(authKey);
    }

    /**
     * 获取redis AuthKey
     *
     * @param token
     * @return
     */
    private String getAuthKey(String token) {
        return redisKeyPrefixConfig.getAuthCode() + ":" + token;
    }
}
