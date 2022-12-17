package ai.fasion.fabs.diana.service;


import ai.fasion.fabs.diana.domain.dto.UserInfoDTO;
import ai.fasion.fabs.diana.domain.po.UserInfoPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;

import java.util.Map;

public interface UserInfoService {

    /**
     * 获取用户列表
     * @param pageRequest
     * @return
     */
    AllInfoVO selectAll(String phone, String uid, PageRequest pageRequest);

    /**
     * 通过用户id获取用户信息
     * @param uid
     * @return
     */
    UserInfoPO findById(String uid);


    /**
     * 修改用户信息
     * @param userInfoDTO
     * @return
     */
    Map<String, Object> updateUserInfo(UserInfoDTO userInfoDTO);

}
