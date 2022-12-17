package ai.fasion.fabs.diana.service;

import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;

import java.util.Map;

public interface TaskService {

    AllInfoVO findAll(String ownerId, PageRequest pageRequest, String type);

    Map<String, Object> findByTaskId(String taskId);
}
