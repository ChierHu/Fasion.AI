package ai.fasion.fabs.diana.mapper;

import ai.fasion.fabs.diana.domain.po.PaymentPO;
import ai.fasion.fabs.diana.domain.vo.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentMapper {

    @Select(" <script>" +
            " SELECT id, uid, type, cash, point, status, channel, created_at, updated_at, finished_at" +
            " FROM payment " +
            " WHERE 1=1 " +
            " <if test = \"type != null and type != ''\">" +
            " AND type = #{type}" +
            " </if>" +
            " <if test = \"uid != null and uid != ''\">" +
            " AND uid = #{uid}" +
            " </if>" +
            " ORDER BY created_at DESC" +
            " </script>")
    List<PaymentVO> paymentList(String uid, String type);

    /*获取用户点数余额*/
    @Select(" SELECT COALESCE(SUM(point),0) AS points FROM payment WHERE  uid = #{uid} ")
    Integer pointSUM(String uid);

    @Select(" SELECT id, uid, type, cash, point, status, channel, purchase_id, related_to, meta, created_at, updated_at, finished_at, depends_on AS dependsOn, slot FROM payment " +
            " WHERE  id = #{paymentId}")
    PaymentPO findById(String paymentId);


}
