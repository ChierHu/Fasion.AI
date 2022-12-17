package ai.fasion.fabs.vesta.enums

import ai.fasion.fabs.vesta.NoArg
import ai.fasion.fabs.vesta.Utils
import ai.fasion.fabs.vesta.enums.Task.Type.Companion.FACE_SWAP
import ai.fasion.fabs.vesta.enums.Task.Type.Companion.MATTING_IMAGE
import ai.fasion.fabs.vesta.utils.SnowflakeUtil
import com.fasterxml.jackson.annotation.*
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet


@NoArg
data class Task(

    val id: String = Utils.hashId(SnowflakeUtil.nextId()),

    val ownerType: String,

    val ownerId: String,

    val type: Type,

    val status: Status = Status.Pending,

    val payload: String, // JSON

    val details: String? = null, // JSON

    val purchaseId: String? = null,

    var point: Int? =null,

    var taskId: String? =null,
) {

    /*@JsonIgnore
    val intOwnerId = ownerId.toLong()*/

    enum class Type(val code: Int) {
        FaceSwap(0),
        MattingImage(1);


        val label
            @JsonValue get() = when (this) {
                FaceSwap -> FACE_SWAP
                MattingImage -> MATTING_IMAGE
            }

        companion object {
            @JsonCreator
            @JvmStatic
            fun taskTypeOf(label: String) = when (label) {
                FaceSwap.label -> FaceSwap
                MattingImage.label -> MattingImage
                else -> throw IllegalArgumentException("Unknown task type: $label")
            }

            const val FACE_SWAP = "face-swap"
            const val MATTING_IMAGE = "matting-image"
        }

        class TypeHandler : BaseTypeHandler<Type>() {
            override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Type, jdbcType: JdbcType?) =
                ps.setInt(i, parameter.code)

            override fun getNullableResult(rs: ResultSet, columnName: String?) =
                values().find { it.code == rs.getInt(columnName) }

            override fun getNullableResult(rs: ResultSet, columnIndex: Int) =
                values().find { it.code == rs.getInt(columnIndex) }

            override fun getNullableResult(cs: CallableStatement, columnIndex: Int) =
                values().find { it.code == cs.getInt(columnIndex) }
        }
    }

    enum class Status(val code: Int) {
        Created(9), //创建待付款
        Pending(0), //待执行
        Claimed(1), //任务已被领取
        Succeed(2), //任务执行成功
        Stopped(3), //任务执行失败
        Removed(-1); //任务移除

        val label @JsonValue get() = name.toLowerCase()

        companion object {
            @JsonCreator
            @JvmStatic
            fun taskStatusOf(label: String) = values().find { it.label == label }
        }

        class TypeHandler : BaseTypeHandler<Status>() {
            override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Status, jdbcType: JdbcType?) =
                ps.setInt(i, parameter.code)

            override fun getNullableResult(rs: ResultSet, columnName: String?) =
                values().find { it.code == rs.getInt(columnName) }

            override fun getNullableResult(rs: ResultSet, columnIndex: Int) =
                values().find { it.code == rs.getInt(columnIndex) }

            override fun getNullableResult(cs: CallableStatement, columnIndex: Int) =
                values().find { it.code == cs.getInt(columnIndex) }
        }
    }

    interface Payload {
        data class FaceSwap(
            val source: String,
            val target: String,
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "type")
    @JsonSubTypes(
        JsonSubTypes.Type(name = FACE_SWAP, value = CreateRequest.FaceSwap::class),
        JsonSubTypes.Type(name = MATTING_IMAGE, value = CreateRequest.MattingImage::class),
    )
    interface CreateRequest<T> {
        val type: Type
        val payload: T

        val ownerType: String
        val ownerId: String

        data class FaceSwap(
            override val type: Type,
            override val payload: List<Payload.FaceSwap>,
            override val ownerType: String,
            override val ownerId: String,
        ) : CreateRequest<List<Payload.FaceSwap>>

        data class MattingImage(
            override val type: Type,
            override val payload: List<Any>,
            override val ownerType: String,
            override val ownerId: String,
        ) : CreateRequest<List<Any>>
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "type")
    @JsonSubTypes(
        JsonSubTypes.Type(name = FACE_SWAP, value = CloseRequest.FaceSwapData::class),
        JsonSubTypes.Type(name = MATTING_IMAGE, value = CloseRequest.MattingOutputData::class),
    )
    interface CloseRequest<T> {
        val type: Type
        val status: Status
        val details: T?

        data class FaceSwapData(
            override val type: Type,
            override val status: Status,
            override val details: FaceSwapInfo?
        ) : CloseRequest<FaceSwapInfo>

        data class MattingOutputData(
            override val type: Type,
            override val status: Status,
            override val details: MattingOutput?
        ) : CloseRequest<MattingOutput>
    }

    data class FaceSwapInfo(val data: List<FaceSwap>)
    data class FaceSwap(val source: String, val target: String, val product: String = "")
    data class MattingOutput(val source: String, val product: String = "")

}

data class TaskOutput(
    val id: String = Utils.hashId(SnowflakeUtil.nextId()),

    val ownerType: String,

    val ownerId: String,

    val assetType: Asset.Type,

    val taskId: String,

    val path: String,

    val status: Int = 1,
)


