package ai.fasion.fabs.diana.service;

import ai.fasion.fabs.diana.domain.vo.StaffsVO;

public interface StaffsService {

    /**
     * 通过管理员id查询管理员信息
     * @param id
     * @return
     */
    StaffsVO staffsInfo(String id);
}
