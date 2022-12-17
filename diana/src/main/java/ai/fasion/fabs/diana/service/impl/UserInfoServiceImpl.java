package ai.fasion.fabs.diana.service.impl;


import ai.fasion.fabs.diana.common.ReturnFormat;
import ai.fasion.fabs.diana.domain.dto.UserInfoDTO;
import ai.fasion.fabs.diana.domain.po.UserInfoPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.domain.vo.ApplicationVO;
import ai.fasion.fabs.diana.domain.vo.UserInfoVO;
import ai.fasion.fabs.diana.mapper.PaymentMapper;
import ai.fasion.fabs.diana.mapper.UserInfoMapper;
import ai.fasion.fabs.diana.service.UserInfoService;
import ai.fasion.fabs.vesta.expansion.FailException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoMapper userInfoMapper;
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;
    private final ReturnFormat returnFormat;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper, ObjectMapper objectMapper, PaymentMapper paymentMapper, ReturnFormat returnFormat) {
        this.userInfoMapper = userInfoMapper;
        this.objectMapper = objectMapper;
        this.paymentMapper = paymentMapper;
        this.returnFormat = returnFormat;
    }

    @Override
    public AllInfoVO selectAll(String phone, String uid, PageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<UserInfoPO> list = userInfoMapper.selectAll(phone, uid);
        AllInfoVO allInfoVO = returnFormat.format(list);
        List<UserInfoVO> userInfoVOList = new ArrayList<>(list.size());
        list.forEach(v -> {
            UserInfoVO userInfoVO = new UserInfoVO();
            //通过uid查询用户的点数余额
            int result = paymentMapper.pointSUM(v.getId());
            userInfoVO.setPoint(result);
            userInfoVO.setUid(v.getId());
            userInfoVO.setNickname(v.getNickname());
            userInfoVO.setAvatar(v.getAvatar());
            userInfoVO.setPhone(v.getPhone());
            userInfoVO.setStatus(v.getStatus());
            userInfoVO.setCreatedAt(v.getCreatedAt());
            userInfoVO.setUpdatedAt(v.getUpdatedAt());
            userInfoVO.setName(v.getName());
            userInfoVO.setCompany(v.getCompany());
            userInfoVO.setReqNote(v.getReqNote());
            userInfoVOList.add(userInfoVO);
        });
        allInfoVO.setData(userInfoVOList);
        return allInfoVO;
    }

    @Override
    public UserInfoPO findById(String uid) {
        UserInfoPO userInfoPO = userInfoMapper.findById(uid);
        return userInfoPO;
    }

    @Override
    public Map<String, Object> updateUserInfo(UserInfoDTO userInfoDTO) {
        ApplicationVO applicationVO = new ApplicationVO();
        applicationVO.setName(userInfoDTO.getName());
        applicationVO.setCompany(userInfoDTO.getCompany());
        applicationVO.setReq_note(userInfoDTO.getReqNote());
        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put("application", applicationVO);
        String jsonMeta = null;
        try {
            //map转换成json保存到数据库
            jsonMeta = objectMapper.writeValueAsString(metaMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //把转换后的json保存到数据库
        userInfoDTO.setMeta(jsonMeta);
        int count = userInfoMapper.updateUserInfo(userInfoDTO);
        if (count == 0) {
            throw new FailException("更新失败");
        }
        //更新成功后返回信息
        UserInfoPO userInfoPO = userInfoMapper.findById(userInfoDTO.getUid());
        Map<String, Object> map = new HashMap<>(3);
        map.put("uid", userInfoPO.getId());
        map.put("nickname", userInfoPO.getNickname());
        map.put("avatar", userInfoPO.getAvatar());
        return map;
    }
}
