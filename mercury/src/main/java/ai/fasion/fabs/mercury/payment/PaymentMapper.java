package ai.fasion.fabs.mercury.payment;

import ai.fasion.fabs.mercury.payment.po.PaymentPO;
import ai.fasion.fabs.mercury.payment.vo.PaymentVO;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.payment.vo.PurchaseOtherVO;
import ai.fasion.fabs.mercury.payment.vo.PurchaseVO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface PaymentMapper {

    @Select(" SELECT pm.id, s.name, pm.type, pm.cash, point, pm.channel, pm.status, pm.created_at, pm.updated_at, pm.finished_at" +
            " FROM payment pm" +
            " LEFT JOIN purchase p ON pm.purchase_id = p.id" +
            " LEFT JOIN sku s ON p.sku_id = s.id" +
            " WHERE pm.uid = #{uid}" +
            " AND pm.type = #{type}")
    List<PaymentVO> selectPayments(String uid, String type);

    @Insert(" INSERT INTO payment (id, uid, type, cash, point, status, channel, purchase_id, related_to, meta,depends_on, slot) VALUES (#{id}, #{uid}, #{type}, #{cash}, #{point}, #{status}, #{channel}, #{purchaseId}, #{relatedTo}, #{meta}::jsonb ,#{dependsOn},#{slot}) ")
    int addPayment(PaymentPO paymentPO);

    @Insert(" INSERT INTO purchase (id,uid, payments, amount, sku_id, product_id, status) VALUES (#{id},#{uid}, #{payments,jdbcType=OTHER, typeHandler = ai.fasion.fabs.vesta.utils.PgStringArrayTypeHandler },#{amount}, #{skuId}, #{productId}, #{status} ) ")
    int addPurchase(PurchasePO purchasePO);

//    @Select(" SELECT s.name, pc.id, pc.created_at, pm.point, pm.cash, pc.status FROM sku s" +
//            " LEFT JOIN purchase pc  ON s.id = pc.sku_id" +
//            " LEFT JOIN payment pm ON pc.product_id = pm.id" +
//            " WHERE pc.id = #{id} AND pc.uid = #{uid}")
//    PurchaseVO findPurchase(String id, String uid);

    @Select(" SELECT s.name, pc.id, pc.created_at, pm.point, pm.cash, pc.status, pm.channel, pc.amount FROM sku s" +
            " LEFT JOIN purchase pc  ON s.id = pc.sku_id" +
            " LEFT JOIN payment pm ON pc.product_id = pm.id" +
            " WHERE s.type = #{type} AND pc.uid = #{uid} AND pm.status != #{status}" +
            " ORDER BY pc.created_at DESC ")
    List<PurchaseVO> purchaseList(String type, String uid, String status);

    @Select(" SELECT s.name, pc.id, pc.created_at, pm.point, pm.cash, pc.status, pm.channel, pc.amount FROM sku s" +
            " LEFT JOIN purchase pc  ON s.id = pc.sku_id" +
            " LEFT JOIN payment pm ON pc.id = pm.purchase_id" +
            " WHERE pc.sku_id IN (SELECT id FROM sku WHERE sku.sku = #{type}) AND pc.uid = #{uid} AND pm.status != #{status}" +
            " ORDER BY pc.created_at DESC ")
    List<PurchaseVO> purchaseTypeList(String type, String uid, String status);

    @Select(" SELECT pc.id, pc.created_at, s.type, pc.amount, pm.point FROM sku s" +
            " LEFT JOIN purchase pc  ON s.id = pc.sku_id" +
            " LEFT JOIN payment pm ON pc.product_id = pm.id" +
            " WHERE s.type = #{type} AND pc.uid = #{uid} AND pm.status != #{status}" +
            " ORDER BY pc.created_at DESC")
    List<PurchaseOtherVO> purchaseListOther(String type, String uid, String status);

    @Select(" SELECT id,uid,type,cash,point,status,channel,purchase_id,related_to,meta,created_at,updated_at,finished_at, depends_on, slot " +
            " FROM payment WHERE id=#{id} AND uid=#{uid}")
    PaymentPO getPaymentById(String id, String uid);

    @Select(" SELECT id, uid, payments, sku_id AS skuId, amount, shipped, product_id AS productId, status,created_at AS createdAt, updated_at AS updatedAt, finished_at AS finishedAt" +
            " FROM purchase WHERE id=#{id} AND uid=#{uid} ")
    @Results({
            @Result(column = "payments", property = "payments", typeHandler = ai.fasion.fabs.vesta.utils.PgStringArrayTypeHandler.class)
    })
    PurchasePO getPurchaseById(String id, String uid);


    @Update("UPDATE payment SET status=#{status} WHERE id=#{id} AND uid=#{uid}")
    int updatePaymentStatusByIdAndUid(String id, String uid, String status);

    @Update("UPDATE purchase SET status=#{status},product_id=#{productId} WHERE id=#{id} AND uid=#{uid} ")
    int updatePurchaseStatusAndProductIdByid(String id, String uid, String status, String productId);

    /**
     * 根据paymentId查询所有
     *
     * @param paymentId
     * @return
     */
    @Select(" SELECT id, uid, type, cash, point, status, channel, purchase_id AS purchaseId, related_to AS relatedTo, meta, created_at AS createdAt, updated_at AS updatedAt, finished_at AS finishedAt, depends_on AS dependsOn, slot " +
            " FROM payment " +
            " WHERE  id = #{paymentId}")
    PaymentPO findPaymentByPaymentId(String paymentId);

    @Select(" SELECT id, uid, type, cash, point, status, channel, purchase_id, related_to, meta, created_at, updated_at, finished_at ,depends_on AS dependsOn, slot" +
            " FROM payment" +
            " WHERE  purchase_id = #{purchaseId}")
    List<PaymentPO> listPayment(String purchaseId);


    @Update(" UPDATE payment SET status=#{status} WHERE id=#{id}")
    int updatePaymentStatusById(String id, String status);

    /**
     * 获取当前用户最新的一条消费记录
     *
     * @param uid
     * @param type
     * @return
     */
    @Select(" SELECT id, uid,type,cash,point,status,channel,purchase_id,related_to,meta,created_at,updated_at,finished_at,depends_on,slot " +
            " FROM payment " +
            " WHERE uid = #{uid} AND type=#{type} " +
            " ORDER BY created_at DESC " +
            " LIMIT 1 ")
    PaymentPO getLastCurrentUserCostRecord(String uid, String type);

    @Select(" SELECT id, uid,type,cash,point,status,channel,purchase_id,related_to,meta,created_at,updated_at,finished_at,depends_on,slot " +
            " FROM payment " +
            " WHERE created_at > #{date} AND uid=#{uid} AND point>0 ")
    List<PaymentPO> listLastUserRechargeRecord(String uid, Date date);


    /**
     * 根据用户id和slot就算用户子钱包剩余点数
     *
     * @return
     */
    @Select("SELECT COALESCE(SUM(point),0) AS points FROM payment WHERE  uid = #{uid} AND slot=#{slot}")
    int countPaymentByUidAndSlot(String uid, String slot);


}
