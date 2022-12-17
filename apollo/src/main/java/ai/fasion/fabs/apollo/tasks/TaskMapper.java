package ai.fasion.fabs.apollo.tasks;

import ai.fasion.fabs.apollo.tasks.dto.TaskDTO;
import ai.fasion.fabs.apollo.tasks.po.TaskPO;
import ai.fasion.fabs.vesta.enums.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {

    @Select(" <script>" +
            " SELECT id, owner, owner_id, type, status, details, payload, pt_log_id, started_at, finished_at, created_at, updated_at " +
            " FROM task " +
            " WHERE owner_id = #{uid} " +
            " AND status IN ( #{pendingStatus}, #{claimedStatus}, #{succeedStatus}, #{stoppedStatus} )" +
            " AND type = #{type} " +
            " AND json_array_element(payload, 0) ->> 'special' IS NULL" +
            " <if test = \"since != null and since != '' and until != null and until != '' \"> " +
            " AND (created_at::date BETWEEN to_date(#{since}, 'YYYY-MM-DD') AND to_date(#{until}, 'YYYY-MM-DD'))" +
            " </if> " +
            " ORDER BY created_at DESC " +
            " </script>")
    @Results({
            @Result(column = "status", property = "status", typeHandler = Task.Status.TypeHandler.class),
            @Result(column = "type", property = "type", typeHandler = Task.Type.TypeHandler.class)
    })
    List<TaskPO> selectByType(String uid, Integer pendingStatus, Integer claimedStatus, Integer succeedStatus, Integer stoppedStatus, Integer type, String since, String until);

    @Select(" SELECT id, owner, owner_id, type, status, details, payload, pt_log_id, started_at, finished_at, created_at, updated_at " +
            " FROM task " +
            " WHERE owner_id = #{uid} AND status != #{status} AND  id = #{taskId} ")
    @Results({
            @Result(column = "status", property = "status", typeHandler = Task.Status.TypeHandler.class),
            @Result(column = "type", property = "type", typeHandler = Task.Type.TypeHandler.class)
    })
    TaskPO findByTaskId(String uid, Integer status, String taskId);
}
