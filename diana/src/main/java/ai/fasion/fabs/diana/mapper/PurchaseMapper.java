package ai.fasion.fabs.diana.mapper;

import ai.fasion.fabs.diana.domain.vo.PurchaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    @Select(" <script>" +
            " SELECT p.id, p.created_at, s.type, p.amount, p.shipped, p.payments, p.status FROM purchase p, sku s" +
            " WHERE p.sku_id = s.id " +
            " <if test = \"type != null and type != ''\">" +
            " AND s.type = #{type}" +
            " </if>" +
            " ORDER BY p.created_at DESC" +
            " </script>")
    @Results({
            @Result(column = "payments", property = "payments", typeHandler = ai.fasion.fabs.vesta.utils.PgStringArrayTypeHandler.class)
    })
    //查询point-pack状态下的订单(purchase)
    List<PurchaseVO> purchaseList(String type);


    @Select(" SELECT p.id, p.created_at, s.type, p.amount, p.shipped, p.payments, p.status FROM purchase p, sku s" +
            " WHERE p.sku_id = s.id AND p.sku_id IN (SELECT id FROM sku WHERE sku.sku = #{type}) ")
    @Results({
            @Result(column = "payments", property = "payments", typeHandler = ai.fasion.fabs.vesta.utils.PgStringArrayTypeHandler.class)
    })
    //查询FaceSwap，MattingImage订单类型下的数据
    List<PurchaseVO> purchaseTypeList(String type);
}
