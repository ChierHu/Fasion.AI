package ai.fasion.fabs.mercury.point;

import ai.fasion.fabs.mercury.point.po.SkuPO;
import ai.fasion.fabs.mercury.point.vo.SkuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Function:点数mapper
 *
 * @author miluo
 * Date: 2021/8/19 17:40
 * @since JDK 1.8
 */
@Mapper
public interface PointMapper {

    /**
     * 获取所有sku信息
     *
     * @return
     */
    @Select(" SELECT s.id, s.sku, s.name, s.slogan, s.price, s.props::json->>'points' AS points, props::json->>'expiration_period' AS expirationPeriod " +
            " FROM (SELECT *, row_number() OVER ( PARTITION BY  sku.sku ORDER BY  created_at DESC) AS group_idx FROM sku) s " +
            " WHERE s.group_idx =1 AND s.status = #{status} AND type = #{type} ")
    List<SkuVO> selectPointsPacks(String status, String type);


    /**
     * 根据skuId获取套餐的价格
     *
     * @param skuId
     * @return
     */
    @Select("SELECT id, name, slogan, price, props::json->>'points' AS points, status, type FROM sku WHERE id = #{skuId}")
    SkuPO getPointPriceBySkuId(String skuId);

    /**
     * 根据用户id获取用户点数信息
     *
     * @param uid
     * @return
     */
    @Select("SELECT COALESCE(SUM(point),0) AS points FROM payment WHERE uid = #{uid} ")
    int getUserPoints(String uid);

    /**
     * 根据用户id获取用户点数信息
     *
     * @param id
     * @return
     */
    @Select("SELECT props::json->>'expiration_period' AS expire FROM sku WHERE id =#{id} ")
    int getPointExpiredAtById(String id);

}
