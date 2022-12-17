package ai.fasion.fabs.vesta.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Function:
 *
 * @author miluo
 *Date: 2021/6/9 13:42
 * @since JDK 1.8
 */
class Status {

    enum class Type(val code: Int) {
        Enable(1),
        Disable(2),
        Delete(3),
        Expire(4);


        val label
            @JsonValue get() = when (this) {
                Enable -> ENABLE
                Disable -> DISABLE
                Delete -> DELETE
                Expire -> EXPIRE
            }

        companion object {
            @JsonCreator
            @JvmStatic
            fun statusTypeOf(label: String) = when (label) {
                Enable.label -> ENABLE
                Disable.label -> DISABLE
                Delete.label -> DELETE
                Expire.label -> EXPIRE
                else -> throw IllegalArgumentException("Unknown task type: $label")
            }

            const val ENABLE = "enable"
            const val DISABLE = "disable"
            const val DELETE = "delete"
            const val EXPIRE = "expire"
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