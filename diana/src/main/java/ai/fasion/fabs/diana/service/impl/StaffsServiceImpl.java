package ai.fasion.fabs.diana.service.impl;

import ai.fasion.fabs.diana.constant.KsOssConstant;
import ai.fasion.fabs.diana.domain.po.AdminUserPO;
import ai.fasion.fabs.diana.domain.vo.StaffsVO;
import ai.fasion.fabs.diana.mapper.AdminUserMapper;
import ai.fasion.fabs.diana.service.StaffsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class StaffsServiceImpl implements StaffsService {

    private final AdminUserMapper adminUserMapper;
    private final KsOssConstant ksOssConstant;

    public StaffsServiceImpl(AdminUserMapper adminUserMapper, KsOssConstant ksOssConstant) {
        this.adminUserMapper = adminUserMapper;
        this.ksOssConstant = ksOssConstant;
    }

    @Override
    public StaffsVO staffsInfo(String id) {
        //用管理员id查询管理员详情
        AdminUserPO adminUserPO = adminUserMapper.findOneById(id);
        StaffsVO staffsVO = new StaffsVO();
        BeanUtils.copyProperties(adminUserPO, staffsVO);
        // 默认一个用户后台管理头像
        staffsVO.setAvatar("http://" + ksOssConstant.getBucketDomain() + "/" + ksOssConstant.getDefaultAvatar());
        return staffsVO;
    }


}
