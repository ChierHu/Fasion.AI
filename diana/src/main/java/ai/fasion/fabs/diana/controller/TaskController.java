package ai.fasion.fabs.diana.controller;

import ai.fasion.fabs.diana.common.PageRequestInfo;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api("任务管理")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    private final PageRequestInfo pageRequestInfo;

    public TaskController(TaskService taskService, PageRequestInfo pageRequestInfo) {
        this.taskService = taskService;
        this.pageRequestInfo = pageRequestInfo;
    }

    @ApiOperation(value = "获取任务列表")
    @GetMapping
    public ResponseEntity<Object> getTasks(@RequestParam(value = "ownerId", required = false ) String ownerId, @RequestParam(value = "type", required = false) String type,
                                           @RequestParam(value = "page", required = false) Integer page) {
        PageRequest pageRequest = pageRequestInfo.pageRequest(page);
        AllInfoVO allTaskInfo = taskService.findAll(ownerId, pageRequest, type);
        return new ResponseEntity<>(allTaskInfo, HttpStatus.OK);
    }

    @ApiOperation("获取任务详情")
    @GetMapping("/{taskId}")
    public ResponseEntity<Object> findByTaskId(@PathVariable(value = "taskId", required = false) String taskId) {
        Map<String, Object> map = taskService.findByTaskId(taskId);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
