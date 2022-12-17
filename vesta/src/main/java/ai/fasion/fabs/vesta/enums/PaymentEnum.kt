package ai.fasion.fabs.vesta.enums

import com.fasterxml.jackson.annotation.JsonCreator
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class PaymentEnum {
    interface Labeled {
        val label: String
    }

    class TypeHandler : BaseTypeHandler<Labeled>() {

        override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Labeled, jdbcType: JdbcType?) =
            ps.setString(i, parameter.label)

        override fun getNullableResult(rs: ResultSet, columnName: String?) =
            Channel.values().find { it.label == rs.getString(columnName) }

        override fun getNullableResult(rs: ResultSet, columnIndex: Int) =
            Channel.values().find { it.label == rs.getString(columnIndex) }

        override fun getNullableResult(cs: CallableStatement, columnIndex: Int) =
            Channel.values().find { it.label == cs.getString(columnIndex) }
    }


    enum class Type(val label: String) {
        Recharge("income"), Redeem("redeem"), Refund("refund"), Spending("spending")
    }

    enum class Status(val label: String) {
        Pending("pending"), Succeed("succeed"), Failed("failed"), Canceled("canceled"), Refunding("refunding"), Refunded(
            "refunded"
        ),
        Unknown("unknown")
    }


    enum class Channel(override val label: String) : Labeled {
        WeChat("wechat"), Alipay("alipay");


        companion object {
            @JsonCreator
            @JvmStatic
            fun channelOf(label: String) = Channel.values().find { it.label == label}
        }
    }




}