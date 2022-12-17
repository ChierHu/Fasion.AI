package ai.fasion.fabs.mercury.profile;

import ai.fasion.fabs.mercury.payment.MetaInfo;

/**
 * Function: 账号管理service
 *
 * @author yangzhiyuan Date: 2021-01-21 10:52:39
 * @since JDK 1.8
 */
public interface UserService {

    MetaInfo.UserSnapshot findOne(String uid);


}
