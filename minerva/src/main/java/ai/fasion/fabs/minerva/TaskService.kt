package ai.fasion.fabs.minerva

import ai.fasion.fabs.minerva.constant.KsOssConstant
import ai.fasion.fabs.minerva.domain.ConfigInfo
import ai.fasion.fabs.minerva.domain.EvaluationInfo
import ai.fasion.fabs.vesta.enums.Asset.Type.Companion.assetTypeOf
import ai.fasion.fabs.vesta.enums.Task
import ai.fasion.fabs.vesta.enums.TaskOutput
import ai.fasion.fabs.vesta.expansion.FailException
import ai.fasion.fabs.vesta.model.JSONTypeHandlerPg
import ai.fasion.fabs.vesta.utils.KsOssUtil
import ai.fasion.fabs.vesta.utils.RestTemplateUtils
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ksyun.ks3.AutoAbortInputStream
import com.ksyun.ks3.dto.GetObjectResult
import org.apache.ibatis.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.concurrent.TimeUnit


interface TaskService {
    fun submit(task: Task): Task

    fun checkout(uid: String, taskId: String, purchaseId: String): ResponseEntity<String>

    fun nextTask(type: Task.Type): Task?
    fun pathOf(assetId: String): String?

    fun submitOutput(taskId: String, outputType: String, keys: List<String>): List<TaskOutput>
    fun closeTask(task: Task)

    fun findTask(taskId: String): Task?

    fun judgeTaskType(task: Task)
}

@Service
class TaskServiceImpl(
    private val taskCacheRedis: RedisTemplate<String, Task>,
    private val taskQueueRedis: StringRedisTemplate,
    private val taskStore: TaskMapper,
    private val assetStore: AssetMapper,
    @Autowired
    val ksOssConstant: KsOssConstant,
    @Autowired
    val objectMapper: ObjectMapper,

    @Value("\${minerva.mercury.url}")
    val mercuryUrl: String
) : TaskService {

    companion object {
        private fun Task.Type.keyOf(status: Task.Status) = "${label}:${status.label}"

        private val Task.Type.taskQueueKey get() = keyOf(Task.Status.Pending)
        private val Task.Type.bufferQueueKey get() = keyOf(Task.Status.Claimed)
    }

    private val taskCacheOps = taskCacheRedis.opsForValue()
    private val taskQueueOps = taskQueueRedis.opsForSet()


    @Transactional(rollbackFor = [RuntimeException::class, Exception::class])
    override fun submit(task: Task) = task.let {
        //sku config
        val text = uploadSkuInfo()
        //sku id
        val minervaInfo: ConfigInfo = objectMapper.readValue(text, ConfigInfo::class.java)
        val sku = minervaInfo.minerva.sku
        val skuId = sku!![task.type.label]
        //amount
        val amount: Int = getAmount(task);
        val url =
            mercuryUrl + "/purchases/evaluation?skuId=" + skuId + "&taskId=" + task.id + "&amount=" + amount + "&uid=" + task.ownerId;
        val post: ResponseEntity<String> = try {
            RestTemplateUtils.post(url, String::class.java)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }

        if (post.statusCode == HttpStatus.OK) {
            val evaluationInfo: EvaluationInfo = objectMapper.readValue(post.body, EvaluationInfo::class.java);
            val tmpTask: Task = task.copy(
                purchaseId = evaluationInfo.purchase_id!!,
                point = evaluationInfo.point!!,
                taskId = evaluationInfo.task_id!!
            )


            taskStore.create(task.copy(status = Task.Status.Created))
            cache(task)
            tmpTask
        } else {
            throw  FailException("创建任务失败！")
        }
    }

    @Transactional(rollbackFor = [RuntimeException::class, Exception::class])
    override fun checkout(uid: String, taskId: String, purchaseId: String): ResponseEntity<String> {
        val url =
            mercuryUrl + "/purchases/cost?taskId=" + taskId + "&purchaseId=" + purchaseId + "&uid=" + uid;
        val post: ResponseEntity<String> = try {
            RestTemplateUtils.post(url, String::class.java)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.PAYMENT_REQUIRED)
        }

        if (post.statusCode == HttpStatus.OK) {
            val taskInfo: Task? = taskStore.findOne(taskId);
            taskStore.update(taskInfo!!.copy(status = Task.Status.Pending))
        }
        return post;
    }

    override fun nextTask(type: Task.Type): Task? = with(type) {
        if (!taskQueueRedis.hasKey(taskQueueKey) // queue expired
            || taskQueueOps.size(taskQueueKey) ?: 0 == 1L // queue drained
        ) fillQueue(type)

        val taskId: String? = taskQueueOps.randomMember(taskQueueKey)

        return when {
            taskId == null -> null
            taskQueueOps.move(
                taskQueueKey, taskId, bufferQueueKey
            ) == true -> findTask(taskId)?.copy(status = Task.Status.Claimed)
            else -> null
        }
    }

    override fun pathOf(assetId: String): String? = assetStore.pathOf(assetId)

    override fun submitOutput(taskId: String, outputType: String, keys: List<String>) =
        findTask(taskId)?.let { task ->
            keys.map { key ->
                TaskOutput(
                    ownerId = task.ownerId,
                    ownerType = task.ownerType,
                    assetType = assetTypeOf(outputType),
                    taskId = taskId,
                    path = key
                )
            }.onEach { assetStore.create(it) }

        } ?: throw IllegalArgumentException("Task not found")

    override fun closeTask(task: Task) {
        // if not claimed (still in bufferQueue), claim it now.
        if (task.status == Task.Status.Claimed) {
            claim(task.id)
        }
        var amount: Int = 0;

        val stoppedTask = taskStore.findOne(task.id)
        //如果任务是失败，需要去计算原本的点数，全部退回
        amount =
            if (task.status == Task.Status.Stopped) getAmount(stoppedTask!!) else getAmount(stoppedTask!!) - getDetails(
                task
            )

        val url =
            mercuryUrl + "/purchases/refundPoint?uid=" + task.ownerId + "&taskId=" + task.id + "&pointAmount=" + amount;
        val post: ResponseEntity<String> = try {
            RestTemplateUtils.post(url, String::class.java)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
        // then update status to finished, error, etc.
        taskStore.close(task)
    }


    /**
     * 得到任务的数量
     */
    private fun getAmount(task: Task): Int {
        var amount = 0;
        //amount
        if (Task.Type.taskTypeOf(task.type.label) == Task.Type.MattingImage) {
            val typeRef = object : TypeReference<List<Task.MattingOutput>>() {}
            val payloadDTOS: List<Task.MattingOutput> = objectMapper.readValue(
                task.payload, typeRef
            );
            amount = payloadDTOS.size;
        } else if (Task.Type.taskTypeOf(task.type.label) == Task.Type.FaceSwap) {
            val typeRef = object : TypeReference<List<Task.FaceSwap>>() {}
            val payloadDTOS: List<Task.FaceSwap> = objectMapper.readValue(task.payload, typeRef)
            amount = payloadDTOS.size;
        }
        return amount;
    }

    /**
     * 得到生成之后的数量
     */
    private fun getDetails(task: Task): Int {
        var amount = 0;
        //amount
        if (Task.Type.taskTypeOf(task.type.label) == Task.Type.MattingImage) {
            val typeRef = object : TypeReference<List<Task.MattingOutput>>() {}
            val payloadDTOS: List<Task.MattingOutput> = objectMapper.readValue(
                task.payload, typeRef
            );
            amount = payloadDTOS.size;
        } else if (Task.Type.taskTypeOf(task.type.label) == Task.Type.FaceSwap) {
            val faceSwapInfo: Task.FaceSwapInfo = objectMapper.readValue(task.details, Task.FaceSwapInfo::class.java)
            amount = faceSwapInfo.data.size;
        }
        return amount;

    }


    override fun findTask(taskId: String): Task? =
        taskCacheOps.get(taskId)
            ?: taskStore.findOne(taskId)
                ?.also { cache(it) }

    override fun judgeTaskType(task: Task) {
        when (task.type) {
            Task.Type.FaceSwap -> {

            }
            Task.Type.MattingImage -> {
            }

        }
    }

    private fun cache(task: Task) {
        taskCacheOps.set(task.id, task, 30, TimeUnit.SECONDS)
    }

    private fun fillQueue(taskType: Task.Type) = with(taskType) {
        taskQueueRedis.expire(taskQueueKey, Duration.ofSeconds(10))

        val buffer = taskQueueOps.members(bufferQueueKey) ?: emptySet()
        val tasks = taskStore.search(taskType, Task.Status.Pending, 100)
            .filterNot { buffer.contains(it.id) }

        if (tasks.isNotEmpty()) {
            taskQueueOps.add(taskQueueKey, *tasks.map { it.id }.toTypedArray())
            tasks.forEach { cache(it) }
        }
    }

    private fun uploadSkuInfo(): String {
        val getObjectResult: GetObjectResult =
            KsOssUtil.getInstance().getObject(ksOssConstant.bucketName, "config/config.json")
        val objectContent: AutoAbortInputStream = getObjectResult.`object`.objectContent
        return objectContent.bufferedReader().use { it.readText() }
    }

    @Scheduled(fixedDelay = 1000)
    fun syncClaimStatus() {
        Task.Type.values().forEach { type ->
            taskQueueOps.members(type.bufferQueueKey)
                ?.forEach { claim(it) }
        }
    }

    @Transactional
    fun claim(taskId: String) =
        taskStore.findOne(taskId)?.let {
            if (it.status == Task.Status.Pending) {
                val claimed = it.copy(status = Task.Status.Claimed)
                taskStore.update(claimed)
                cache(claimed)
            }
            taskQueueOps.remove(it.type.bufferQueueKey, it.id)
        }

}

@Mapper
interface TaskMapper {
    companion object {
        const val tasks = "task"

        private const val JsonTypeHandler = "ai.fasion.fabs.vesta.model.JSONTypeHandlerPg"

        const val props = """
            id, owner, owner_id, type, status, payload, details
        """
        const val mapping = """
            #{id}, #{ownerType}, #{ownerId}, #{type.code}, #{status.code}, 
            #{payload, jdbcType=OTHER, typeHandler=$JsonTypeHandler},
            #{details, jdbcType=OTHER, typeHandler=$JsonTypeHandler}
        """
    }

    @Insert("INSERT INTO $tasks ($props) VALUES ($mapping)")
    fun create(task: Task): Int

    @Select("SELECT $props FROM $tasks WHERE id = #{id}")
    @Results(
        id = "taskResultMap", value = [
            Result(property = "ownerType", column = "owner"),
            Result(property = "type", column = "type", typeHandler = Task.Type.TypeHandler::class),
            Result(property = "status", column = "status", typeHandler = Task.Status.TypeHandler::class),
            Result(property = "payload", column = "payload", typeHandler = JSONTypeHandlerPg::class),
            Result(property = "details", column = "details", typeHandler = JSONTypeHandlerPg::class),
        ]
    )
    fun findOne(taskId: String): Task?

    @Select(
        """
        SELECT $props FROM $tasks
        WHERE type = #{taskType.code} AND status = #{status.code}
        LIMIT #{maxCount}
        """
    )
    @ResultMap("taskResultMap")
    fun search(taskType: Task.Type, status: Task.Status, maxCount: Int): List<Task>

    @Update(
        """
        UPDATE $tasks
        SET status = #{status.code}, started_at = current_timestamp 
        WHERE id = #{id}
        """
    )
    fun update(task: Task)

    @Update(
        """
        UPDATE $tasks
        SET status = #{status.code}, finished_at = current_timestamp , details = #{details, jdbcType=OTHER, typeHandler=$JsonTypeHandler}
        WHERE id = #{id}
        """
    )
    fun close(task: Task)
}

@Mapper
interface AssetMapper {
    companion object Table {
        const val assets = "asset"

        const val props = """
            id, owner, owner_id, type, bundle, path, status
        """
        const val mapping = """
            #{id}, #{ownerType}, #{ownerId}, #{assetType.code}, #{taskId}, #{path}, #{status}
        """
    }

    @Select("INSERT INTO $assets ($props) VALUES ($mapping)")
    fun create(output: TaskOutput)

    @Select("SELECT path FROM $assets WHERE id = #{assetId}")
    fun pathOf(assetId: String): String?
}
