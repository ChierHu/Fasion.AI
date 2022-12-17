package ai.fasion.fabs.minerva

import ai.fasion.fabs.vesta.enums.Task
import ai.fasion.fabs.vesta.enums.Task.Type.Companion.taskTypeOf
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
class TaskController(
    private val taskService: TaskService,
    private val jackson: ObjectMapper,
    @Value("\${minerva.mercury.url}")
    private val mercuryUrl: String
) {

    /**
     * apollo模块提交任务到minerva中，minerva将任务存储到数据库中
     */
    @PostMapping("/tasks")
    fun <T> submit(@RequestBody body: Task.CreateRequest<T>): ResponseEntity<Any> {
        val task = Task(
            type = body.type,
            payload = jackson.writeValueAsString(body.payload),
            ownerType = body.ownerType,
            ownerId = body.ownerId,

            )

        val tasks: Task;
        try {
            tasks = taskService.submit(task)
        } catch (e: Exception) {
            return ResponseEntity(e.message, HttpStatus.PAYMENT_REQUIRED);
        }
        return ResponseEntity(tasks, HttpStatus.OK);
    }

    /**
     * 获取下一个任务信息
     */
    @PostMapping("/tasks/next")
    fun nextTask(@RequestParam("type") taskType: String): ResponseEntity<Task?> {
        val task = taskService.nextTask(type = taskTypeOf(taskType))
        return ResponseEntity(task, if (task != null) HttpStatus.OK else HttpStatus.NO_CONTENT)
    }

    /**
     * 根据资产id获取资产信息
     */
    @GetMapping("/assets/{assetId}")
    fun asset(@PathVariable("assetId") assetId: String): ResponseEntity<String?> {
        val path = taskService.pathOf(assetId)
        return ResponseEntity(path, if (path != null) HttpStatus.OK else HttpStatus.NOT_FOUND)
    }

    /**
     * 根据任务id，将任务结果存储到数据库中
     */
    @PostMapping("/tasks/{taskId}/output")
    fun submitOutput(
        @PathVariable("taskId") taskId: String,
        @RequestParam("type") outputType: String,
        @RequestBody keys: List<String>
    ): List<String> {
        return taskService.submitOutput(taskId, outputType, keys).map { it.id }
    }

    /**
     *根据任务id查询任务需要的资产信息所在的位置
     */
    @GetMapping("/tasks/{taskId}/output/key")
    fun getOutputKey(
        @PathVariable("taskId") taskId: String,
        @RequestParam("type") outputType: String
    ) = taskService.findTask(taskId)?.let {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val typePath = outputType.replace('-', '/')

        "/data/c0/${it.ownerId}/assets/$typePath/$date/$taskId"
    }

    /**
     * 任务执行或出错都需要进行关闭操作
     */
    @PatchMapping("/tasks/{taskId}")
    fun <T> closeTask(@PathVariable("taskId") taskId: String, @RequestBody body: Task.CloseRequest<T>) =
        taskService.findTask(taskId)?.copy(
            status = body.status,
            details = jackson.writeValueAsString(body.details),
        )?.run {
            taskService.closeTask(this)
        }

    /**
     * 执行任务
     */
    @PostMapping("/tasks/{taskId}/checkout")
    fun checkout(
        @PathVariable("taskId") taskId: String,
        @RequestParam("purchaseId") purchaseId: String,
        @RequestParam("uid") uid: String
    ): ResponseEntity<String> {
       return taskService.checkout(uid, taskId, purchaseId)
    }
}