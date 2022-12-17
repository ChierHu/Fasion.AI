package ai.fasion.fabs.apollo.tasks;

import ai.fasion.fabs.apollo.domain.PageRequest;
import ai.fasion.fabs.apollo.tasks.vo.AllTaskInfoVO;
import ai.fasion.fabs.apollo.tasks.vo.SubmitTasksVO;
import ai.fasion.fabs.apollo.tasks.vo.TasksPayloadVO;
import ai.fasion.fabs.apollo.tasks.vo.ZipVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface TasksService {

    AllTaskInfoVO findAll(PageRequest pageRequest, String uid, String type, String since, String until);

    /**
     * 通过taskId查询到task数据
     *
     * @param uid
     * @param taskId
     * @return
     */
    Map<String, Object> findByTaskId(String uid, String taskId);

    SubmitTasksVO submitTask(String type, TasksPayloadVO payload) throws JsonProcessingException;

    ZipVO downloadTasks(String id);

    ResponseEntity<String> checkout(String uid, String taskId, String purchaseId);


}
