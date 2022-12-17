package ai.fasion.fabs.vesta.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.lang.IllegalArgumentException
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Function:
 *
 * @author miluo
 *Date: 2021/6/8 15:28
 * @since JDK 1.8
 */
class Asset {
    enum class Type(val code: Int) {
        FaceSource(1),
        FaceTarget(2),
        MattingImage(3),
        MattingOutput(4),
        FaceOutput(5),
        SystemMattingScene(6),
        SystemFaceSource(7),
        SystemMattingImage(8),
        MattingScene(9);




        val label
            @JsonValue get() = when (this) {
                FaceSource -> FACE_SOURCE
                FaceTarget -> FACE_TARGET
                FaceOutput -> FACE_OUTPUT
                MattingImage -> MATTING_IMAGE
                MattingOutput -> MATTING_OUTPUT
                SystemMattingScene -> SYSTEM_MATTING_SCENE
                SystemFaceSource -> SYSTEM_FACE_SOURCE
                SystemMattingImage -> SYSTEM_MATTING_IMAGE
                MattingScene -> MATTING_SCENE
            }

        companion object {
            @JsonCreator
            @JvmStatic
            fun assetTypeOf(label: String) = when (label) {
                FaceSource.label -> FaceSource
                FaceTarget.label -> FaceTarget
                FaceOutput.label -> FaceOutput
                MattingImage.label -> MattingImage
                MattingOutput.label -> MattingOutput
                SystemMattingScene.label -> SystemMattingScene
                SystemFaceSource.label -> SystemFaceSource
                SystemMattingImage.label -> SystemMattingImage
                MattingScene.label -> MattingScene
                else -> throw IllegalArgumentException("Unknown asset type: $label")
            }

            const val FACE_SOURCE = "face-source"
            const val FACE_TARGET = "face-target"
            const val FACE_OUTPUT = "face-output"
            const val MATTING_IMAGE = "matting-image"
            const val MATTING_OUTPUT = "matting-output"
            const val SYSTEM_MATTING_SCENE = "system-matting-scene"
            const val SYSTEM_FACE_SOURCE = "system-face-source"
            const val SYSTEM_MATTING_IMAGE = "system-matting-image"
            const val MATTING_SCENE = "matting-scene"
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
}