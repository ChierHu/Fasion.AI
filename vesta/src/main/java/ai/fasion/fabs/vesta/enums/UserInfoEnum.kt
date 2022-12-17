package ai.fasion.fabs.vesta.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.lang.IllegalArgumentException
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class UserInfoEnum {
    enum class Status(val code: Int) {
        Banned(0),
        Active(1),
        Pending(2),
        Created(3);


        val label
            @JsonValue get() = when (this) {
                Banned -> BANNED
                Active -> ACTIVE
                Pending -> PENDING
                Created -> CREATED
            }

        companion object {
            @JsonCreator
            @JvmStatic
            fun statusTypeOf(label: String) = when (label) {
                Banned.label -> Banned
                Active.label -> Active
                Pending.label -> Pending
                Created.label -> Created
                else -> throw IllegalArgumentException("Unknown asset type: $label")
            }

            const val BANNED = "banned"
            const val ACTIVE = "active"
            const val PENDING = "pending"
            const val CREATED = "created"
        }

        class StatusHandler : BaseTypeHandler<Status>() {
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
}