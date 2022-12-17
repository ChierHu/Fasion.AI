package ai.fasion.fabs.mercury.profile;

import ai.fasion.fabs.mercury.payment.MetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Function: 用户管理服务类
 *
 * @author yangzhiyuan Date: 2021-01-21 11:02:23
 * @since JDK 1.8
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserInfoMapper userInfoMapper;

    public UserServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }


    @Override
    public MetaInfo.UserSnapshot findOne(String uid) {
        return userInfoMapper.findOne(uid);
    }
}
