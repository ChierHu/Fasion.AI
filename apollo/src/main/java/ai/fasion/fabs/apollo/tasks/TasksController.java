package ai.fasion.fabs.apollo.tasks;

import ai.fasion.fabs.apollo.domain.PageRequest;
import ai.fasion.fabs.apollo.payment.PaymentController;
import ai.fasion.fabs.apollo.tasks.vo.AllTaskInfoVO;
import ai.fasion.fabs.apollo.tasks.vo.SubmitTasksVO;
import ai.fasion.fabs.apollo.tasks.vo.TasksPayloadVO;
import ai.fasion.fabs.apollo.tasks.vo.ZipVO;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Function: tasks controller
 *
 * @author miluo
 * Date: 2021/5/28 15:59
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/tasks")
@Api(tags = "任务接口")
public class TasksController {
    private static final Logger log = LoggerFactory.getLogger(TasksController.class);

    private final TasksService tasksService;

    public TasksController(TasksService tasksService) {
        this.tasksService = tasksService;
    }


    @ApiOperation("获取当前用户所有任务")
    @GetMapping
    public ResponseEntity<Object> getTasks(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "type", required = false) String type,
                                           @RequestParam(value = "since", required = false) String since, @RequestParam(value = "until", required = false) String until) {
        log.info("get into [getTasks]");
        if (since == null && until != null) {
            log.error("since is null");
            return new ResponseEntity<>("请输入开始时间", HttpStatus.BAD_REQUEST);
        }
        if (since != null && until == null) {
            log.error("until is null");
            return new ResponseEntity<>("请输入结束时间", HttpStatus.BAD_REQUEST);
        }
        //如果开始时间和结束时间都已经输入或都没输入，进行后续工作进行查询
        String uid = AppThreadLocalHolder.getUserId();
        PageRequest pageRequest;
        if (null == page) {
            log.info("page is null ，use default constructor");
            pageRequest = new PageRequest();
        } else {
            log.info("page is not null ，use two parameter constructor");
            pageRequest = new PageRequest(page, 10);
        }

        AllTaskInfoVO allTaskInfoVO = tasksService.findAll(pageRequest, uid, type, since, until);
        return new ResponseEntity<>(allTaskInfoVO, HttpStatus.OK);
    }

    @ApiOperation("获取任务详情")
    @GetMapping("/{taskId}")
    public ResponseEntity<Object> findByTaskId(@PathVariable(value = "taskId", required = false) String taskId) {
        log.info("get into [findByTaskId]");
        Map<String, Object> map = tasksService.findByTaskId(AppThreadLocalHolder.getUserId(), taskId);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @ApiOperation("点数预估")
    @PostMapping
    public ResponseEntity<Object> submitTasks(@RequestParam String type, @RequestBody TasksPayloadVO payload) {
        log.info("get into [submitTasks]");
        try {
            SubmitTasksVO submitTasksVO = tasksService.submitTask(type, payload);
            return new ResponseEntity<>(submitTasksVO, HttpStatus.OK);
        } catch (Exception e) {
            String text = e.getMessage();
            text = text.substring(text.lastIndexOf("[")).replace("]", "").replace("[", "");
            return new ResponseEntity<>(text, HttpStatus.PAYMENT_REQUIRED);
        }

    }


    @ApiOperation("根据任务id下载任务内容")
    @PostMapping("/{id}/download")
    public ResponseEntity<Object> downloadTasks(@PathVariable("id") String id) {
        log.info("get into [downloadTasks]");
        ZipVO zipVO = tasksService.downloadTasks(id);
        return new ResponseEntity<>(zipVO, HttpStatus.OK);
    }


    /**
     * 执行任务
     *
     * @param taskId, purchaseId
     * @return
     */
    @PostMapping("/{taskId}/checkout")
    public ResponseEntity<String> checkout(@PathVariable("taskId") String taskId, @RequestParam("purchaseId") String purchaseId) {
        String uid = AppThreadLocalHolder.getUserId();
        return tasksService.checkout(uid, taskId, purchaseId);
    }

}
