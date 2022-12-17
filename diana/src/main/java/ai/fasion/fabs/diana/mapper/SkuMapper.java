package ai.fasion.fabs.diana.mapper;

import ai.fasion.fabs.diana.domain.po.SkuPO;
import ai.fasion.fabs.diana.domain.vo.SkuInfoVO;
import ai.fasion.fabs.diana.domain.vo.SkuOtherInfoVO;
import ai.fasion.fabs.diana.domain.vo.SkuVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SkuMapper {

    @Select(" <script>" +
            " SELECT s.id, s.sku, s.name, s.slogan, s.price, s.props, s.status " +
            " FROM (SELECT *, row_number() OVER ( PARTITION BY  sku.sku ORDER BY  created_at DESC) AS group_idx FROM sku) s" +
            " WHERE s.group_idx =1" +
            " <if test = \"type == null or type == '' \"> " +
            " AND type = 'point-pack' " +
            " </if> " +
            " <if test = \"type != null and type != '' \"> " +
            " AND type = #{type}" +
            " </if> " +
            " </script>")
    List<SkuPO> skuInfoList(String type);

    @Select(" SELECT s.id, s.sku, s.name, s.slogan, s.price, s.props, s.status " +
            " FROM (SELECT *, row_number() OVER ( PARTITION BY  sku.sku ORDER BY  created_at DESC) AS group_idx FROM sku) s" +
            " WHERE s.group_idx =1 AND s.id= #{id} ")
    SkuOtherInfoVO skuOtherInfoOther(String id);

    @Update(" UPDATE sku SET status = #{status} WHERE id = #{id}")
    int updateStatus(String status, String id);

    @Insert(" INSERT INTO sku" +
            " (id, sku, revision, name, price, type, slogan, status, props) VALUES" +
            " (#{id}, #{sku}, #{revision}, #{name}, #{price}, #{type}, #{slogan}, #{status}, #{props}::jsonb )")
    int insertSku(SkuPO skuPO);

    @Select(" SELECT id, sku, revision, name, price, type, slogan, status, props, created_at FROM sku WHERE id = #{id}")
    SkuPO findSkuByid(String id);

    @Select(" SELECT id, sku, name, price, type, slogan, status, props::json->>'points' AS points, props::json->>'expiration_period' AS expirationPeriod,  created_at FROM sku" +
            " WHERE id = #{id}")
    SkuVO findById(String id);



}
