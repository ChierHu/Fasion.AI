package ai.fasion.fabs.mercury.payment;

import ai.fasion.fabs.mercury.payment.po.PaymentPO;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.payment.po.PurchasePackInfoVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    /**
     * 根据purchaseId查询purchase表数据，查询所有字段(精确查询)
     *
     * @param purchaseId
     * @return
     */
    @Select(" SELECT id, uid, payments, sku_id, amount, shipped, product_id, status, created_at, updated_at, finished_at" +
            " FROM purchase" +
            " WHERE id = #{purchaseId}")
    @Results({
            @Result(column = "payments", property = "payments", typeHandler = ai.fasion.fabs.vesta.utils.PgStringArrayTypeHandler.class)
    })
    PurchasePO findByPurchaseId(String purchaseId);


    /**
     * 根据product_id查询订单信息
     *
     * @param productId
     * @return
     */
    @Select(" SELECT id, uid, payments, sku_id, amount, shipped, product_id, status, created_at, updated_at, finished_at" +
            " FROM purchase" +
            " WHERE product_id = #{productId}")
    @Results({
            @Result(column = "payments", property = "payments", typeHandler = ai.fasion.fabs.vesta.utils.PgStringArrayTypeHandler.class)
    })
    PurchasePO findByPurchaseByProductId(String productId);

    /**
     * 根据id查询purchase表中所有和id关联的paymentId的数据(这是一个list)
     *
     * @param id
     * @return
     */
    @Select(" SELECT id, uid, payments, sku_id, amount, shipped, product_id, status, created_at, updated_at, finished_at" +
            " FROM purchase" +
            " WHERE #{id} = ANY (payments) ")
    List<PurchasePO> findById(String id);

    /**
     * 根据订单id修改状态
     *
     * @param id
     * @return
     */
    @Update(" UPDATE purchase SET status=#{status} WHERE id=#{id}")
    int updatePurchaseStatusById(String id, String status);

    @Select(" SELECT id, uid, payments, sku_id, amount, shipped, product_id, status, created_at, updated_at, finished_at" +
            " FROM purchase" +
            " WHERE uid = #{uid} AND status = #{status} AND sku_id in (SELECT id FROM sku WHERE type = #{skuType} AND status = #{skuStatus})")
    List<PurchasePO> listPurchaseByUidStatusSkuId(String uid, String status, String skuType, String skuStatus);


    /**
     * 得到用户时间最久远的一条，记录
     *
     * @param uid
     * @param purchaseIdStatus
     * @param skuType
     * @param skuStatus
     * @return
     */
    @Select("SELECT id, uid, payments, sku_id, amount, shipped, product_id, status, created_at, updated_at, finished_at " +
            "FROM purchase " +
            "WHERE uid = #{uid} AND status = #{purchaseIdStatus} AND sku_id in (SELECT id FROM sku WHERE type =#{skuType} AND status = #{skuStatus}) " +
            "ORDER BY created_at DESC ")
    PurchasePO getCurrentLastPurchaseRecord(String uid, String purchaseIdStatus, String skuType, String skuStatus);


    @Select(" SELECT p.id,p.sku_id,p.amount,p.product_id,p.created_at,p.payments,s.props::json->'points' AS points,s.props::json->'expiration_period' AS expirationPeriod ,s.type " +
            " FROM purchase AS p,sku AS s " +
            " WHERE p.sku_id=s.id AND s.type=#{type} AND p.status='finished' AND uid=#{uid}" +
            " ORDER BY created_at DESC ")
    List<PurchasePackInfoVO> listUserPurchasePointPack(String uid, String type);

    @Select(" SELECT p.id,p.sku_id,p.amount,p.product_id,p.created_at,p.payments,s.props::json->'points' AS points,s.props::json->'expiration_period' AS expirationPeriod ,s.type " +
            " FROM purchase AS p,sku AS s " +
            " WHERE p.sku_id=s.id AND p.id=#{productId} AND p.status='finished' AND uid=#{uid}" +
            " ORDER BY created_at DESC ")
    PurchasePackInfoVO getUserPurchasePointPack(String uid, String productId);

    @Update("UPDATE purchase SET payments=#{payments,jdbcType=OTHER, typeHandler = ai.fasion.fabs.vesta.utils.PgStringArrayTypeHandler} WHERE id=#{id}")
    void updatePurchasePaymentsById(String id, String[] payments);

    @Select(" SELECT id, uid, payments, sku_id, amount, shipped, product_id, status, created_at, updated_at, finished_at" +
            " FROM purchase" +
            " WHERE uid = #{uid} AND status = #{status} AND sku_id in (SELECT id FROM sku WHERE type = #{skuType} AND status = #{skuStatus})")
    List<PaymentPO> listPaymentByUidStatusSkuId(String uid, String status, String skuType, String skuStatus);
}


