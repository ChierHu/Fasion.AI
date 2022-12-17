package ai.fasion.fabs.diana.mapper;

import ai.fasion.fabs.diana.domain.po.TaskPO;
import ai.fasion.fabs.vesta.enums.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper {

    @Select(" <script>" +
            " SELECT id, owner, owner_id, type, status, details, payload, pt_log_id, started_at, finished_at, created_at, updated_at " +
            " FROM task WHERE 1=1" +
            " <if test = \"type != null and type != ''\">" +
            " AND type = #{type} " +
            " </if>" +
            " <if test = \"ownerId != null and ownerId != ''\">" +
            " AND owner_id = #{ownerId}" +
            " </if>" +
            " ORDER BY created_at DESC " +
            " </script>")
    @Results({
            @Result(column = "status", property = "status", typeHandler = Task.Status.TypeHandler.class),
            @Result(column = "type", property = "type", typeHandler = Task.Type.TypeHandler.class)
    })
    List<TaskPO> selectByType(String ownerId, Integer type);


    @Select(" SELECT id, owner, owner_id, type, status, details, payload, pt_log_id, started_at, finished_at, created_at, updated_at " +
            " FROM task WHERE id = #{taskId} ")
    @Results({
            @Result(column = "status", property = "status", typeHandler = Task.Status.TypeHandler.class),
            @Result(column = "type", property = "type", typeHandler = Task.Type.TypeHandler.class)
    })
    TaskPO findByTaskId(String taskId);

}
