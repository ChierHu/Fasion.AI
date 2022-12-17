package ai.fasion.fabs.mercury.payment

import ai.fasion.fabs.mercury.domain.PageRequest
import ai.fasion.fabs.vesta.enums.PaymentEnum.Channel.Companion.channelOf
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.pagehelper.PageHelper
import kotlin.Throws
import java.io.IOException
import ai.fasion.fabs.vesta.enums.SkuEnum
import ai.fasion.fabs.vesta.expansion.FailException
import ai.fasion.fabs.vesta.utils.SnowflakeUtil
import ai.fasion.fabs.vesta.enums.PaymentEnum
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException
import ai.fasion.fabs.vesta.enums.PurchaseEnum
import java.net.URISyntaxException
import ai.fasion.fabs.mercury.wechat.pojo.OrderResult
import com.fasterxml.jackson.core.JsonProcessingException
import ai.fasion.fabs.mercury.payment.MetaInfo.UserSnapshot
import ai.fasion.fabs.mercury.payment.MetaInfo.Meta
import java.util.function.BinaryOperator
import ai.fasion.fabs.mercury.wechat.pojo.RefundResult
import ai.fasion.fabs.mercury.payment.pojo.PurchaseMeta
import ai.fasion.fabs.mercury.payment.PurchasePackDoubleList
import ai.fasion.fabs.mercury.payment.po.PaymentPO
import ai.fasion.fabs.mercury.payment.po.PurchasePO
import ai.fasion.fabs.mercury.payment.po.PurchasePackInfoVO
import ai.fasion.fabs.mercury.payment.pojo.ChannelInfo
import ai.fasion.fabs.mercury.payment.pojo.PaymentInfo
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo
import ai.fasion.fabs.mercury.payment.vo.AllPaymentInfoVO
import ai.fasion.fabs.mercury.payment.vo.LinkVO
import ai.fasion.fabs.mercury.payment.vo.PayMentInfoVO
import ai.fasion.fabs.mercury.payment.vo.PurchaseVO
import ai.fasion.fabs.mercury.point.PointMapper
import ai.fasion.fabs.mercury.point.po.SkuPO
import ai.fasion.fabs.mercury.profile.UserInfoMapper
import ai.fasion.fabs.mercury.wechat.TransactionService
import ai.fasion.fabs.vesta.expansion.NotFoundException
import com.github.pagehelper.PageInfo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.*
import java.util.function.Consumer
import java.util.function.IntFunction
import javax.validation.constraints.NotNull

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/16 17:25
 * @since JDK 1.8
 */
open class PaymentServiceImplDemo(
    private val paymentMapper: PaymentMapper,
    private val pointMapper: PointMapper,
    private val userInfoMapper: UserInfoMapper,
    private val objectMapper: ObjectMapper,
    private val transactionService: TransactionService,
    private val purchaseMapper: PurchaseMapper
) : PaymentService {
    /**
     * 根据uid获取用户下所有支付信息
     *
     * @param pageRequest
     * @param uid
     * @return
     */
    override fun findAllByUid(
        pageRequest: PageRequest,
        uid: String,
        type: String
    ): AllPaymentInfoVO {
        PageHelper.startPage<Any>(pageRequest.page, pageRequest.size)
        val pageInfo =
            PageInfo(
                paymentMapper.selectPayments(uid, type)
            )
        var next: String? = null
        val last = "/tasks?page=" + pageInfo.pages
        if (pageInfo.pageNum < pageInfo.pages) {
            next = "/tasks?page=" + (pageInfo.pageNum + 1)
        }
        val linkVO = LinkVO(next, last)
        return AllPaymentInfoVO(
            pageInfo.total.toInt(), linkVO, pageInfo.list
        )
    }

    /**
     * 创建订单
     *
     * @param uid       用户id
     * @param skuId     套餐id
     * @param channel   渠道
     * @param amount    购买数量
     * @param userAgent UA
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun createOrder(
        uid: String,
        skuId: String,
        channel: String,
        amount: Int,
        userAgent: String
    ): PurchaseInfo {
        //1.根据sku查询sku套餐信息的金额
        val currentPointInfo = pointMapper.getPointPriceBySkuId(skuId)
            ?: throw NotFoundException("没有找到对应的套餐信息！")
        //2. 判断sku的状态是否是启用的状态，如果不是，也不允许操作
        if (currentPointInfo.status == SkuEnum.Status.Disable.label) {
            throw FailException("套餐没有启动，无法进行消费！")
        }

        //3.insert data in payment table and purchase table
        //资金表id
        val paymentId = "pay-" + SnowflakeUtil.nextId()
        //订单表id
        val purchaseId = "pur-" + SnowflakeUtil.nextId()

        //3. 判断sku是支付还是执行
        var qrCodeUrl: String? = null
        var orderId: String? = null
        var paymentIds: String? = null
        var purchasePO = PurchasePO()
        if (SkuEnum.Type.PointPack.label == currentPointInfo.type) {
            val metaJson = generateMetaInfo(uid, null, userAgent)
            val (orderId1, qrCodeUrl1) = getQrCodeUrl(
                uid,
                currentPointInfo,
                channelOf(channel)!!,
                purchaseId,
                paymentId,
                metaJson
            )
            orderId = orderId1
            qrCodeUrl = qrCodeUrl1
            //通过订单id和uid查询
            purchasePO = paymentMapper.getPurchaseById(orderId, uid)
            paymentIds = purchasePO.payments[0]
        } else {
            val metaJson = generateMetaInfo(uid, currentPointInfo.id, userAgent)
            val purchase =
                purchase(uid, userAgent, currentPointInfo, amount, purchaseId, paymentId, metaJson)
            qrCodeUrl = null
            orderId = purchase.id
            purchasePO = paymentMapper.getPurchaseById(orderId, uid)
            paymentIds = purchasePO.productId
        }
        //找到paymentId查询payment表
        val (id, _, type, _, point, status, channel1) = paymentMapper.findPaymentByPaymentId(paymentIds)
        //ChannelInfo类型赋值
        val channelInfo =
            ChannelInfo(channel1, qrCodeUrl)
        //paymentInfo类型赋值
        val paymentInfo =
            PaymentInfo(
                id,
                type,
                point,
                channelInfo,
                status
            )
        //purchaseInfo类型赋值
        return PurchaseInfo(
            purchasePO.id,
            purchasePO.uid,
            paymentInfo,
            purchasePO.skuId,
            purchasePO.amount,
            purchasePO.shipped,
            purchasePO.productId,
            purchasePO.status,
            purchasePO.createdAt,
            purchasePO.updatedAt,
            purchasePO.finishedAt
        )
    }

    /**
     * 发起付款二维码
     *
     * @param uid
     * @param skuInfo
     * @param channel
     * @param purchaseId
     * @param paymentId
     * @param metaJson
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = [RuntimeException::class, Exception::class])
    @Throws(IOException::class)
    override fun getQrCodeUrl(
        uid: String,
        skuInfo: SkuPO,
        channel: PaymentEnum.Channel,
        purchaseId: String,
        paymentId: String,
        metaJson: String
    ): PayMentInfoVO {
        val payments = arrayOf(paymentId)
        //订单表信息
        val purchasePO = PurchasePO(
            purchaseId,
            uid,
            payments,
            Objects.requireNonNull(skuInfo.id),
            1,
            0,
            null,
            PurchaseEnum.Status.Pending.label,
            null,
            null,
            null
        )
        paymentMapper.addPurchase(purchasePO)
        //资金表信息
        val paymentPO = PaymentPO(
            paymentId, uid, PaymentEnum.Type.Recharge.label, skuInfo.price!!, 0, PaymentEnum.Status.Pending.label,
            PaymentEnum.Channel.WeChat.label, purchaseId, null, metaJson, null, null, null, null, null
        )
        paymentMapper.addPayment(paymentPO)

        //2.根据channel判断调起哪个支付平台(Alipay、Wechat)
        return if (PaymentEnum.Channel.WeChat == channel) {
            //4.invoke Wechat payment return QcUrl
            val qcUrl = transactionService.createOrder(purchaseId, skuInfo.price, skuInfo.slogan, "system")
            PayMentInfoVO(purchaseId, qcUrl)
        } else {
            throw NotFoundException("没有找到匹配的渠道！")
        }
    }

    override fun purchaseList(
        uid: String,
        pageRequest: PageRequest
    ): AllPaymentInfoVO {
        PageHelper.startPage<Any>(pageRequest.page, pageRequest.size)
        val list: MutableList<Any> = ArrayList()
        //查询点数套餐(point-pack)订单下的数据
        val pointPackList =
            paymentMapper.purchaseList(PurchaseEnum.Type.PointPack.code, uid, PaymentEnum.Status.Pending.label)
        val faceSwapList =
            paymentMapper.purchaseTypeList(PurchaseEnum.Type.FaceSwap.code, uid, PaymentEnum.Status.Pending.label)
        val mattingImageList =
            paymentMapper.purchaseTypeList(PurchaseEnum.Type.MattingImage.code, uid, PaymentEnum.Status.Pending.label)
        list.addAll(pointPackList)
        list.addAll(faceSwapList)
        list.addAll(mattingImageList)
        val pageInfo = PageInfo(list)
        var next: String? = null
        val last = "/tasks?page=" + pageInfo.pages
        if (pageInfo.pageNum < pageInfo.pages) {
            next = "/tasks?page=" + (pageInfo.pageNum + 1)
        }
        val linkVO = LinkVO(next, last)
        return AllPaymentInfoVO(
            pageInfo.total.toInt(), linkVO, pageInfo.list
        )
    }

    override fun purchaseListByType(
        type: String,
        uid: String,
        pageRequest: PageRequest
    ): AllPaymentInfoVO {
        PageHelper.startPage<Any>(pageRequest.page, pageRequest.size)
        var list: List<PurchaseVO>? = null
        if (PurchaseEnum.Type.PointPack.label == type) {
            //查询点数套餐(point-pack)订单下的数据
            list = paymentMapper.purchaseList(PurchaseEnum.Type.PointPack.code, uid, PaymentEnum.Status.Pending.label)
        }
        if (PurchaseEnum.Type.FaceSwap.label == type) {
            list =
                paymentMapper.purchaseTypeList(PurchaseEnum.Type.FaceSwap.code, uid, PaymentEnum.Status.Pending.label)
        }
        if (PurchaseEnum.Type.MattingImage.label == type) {
            list = paymentMapper.purchaseTypeList(
                PurchaseEnum.Type.MattingImage.code,
                uid,
                PaymentEnum.Status.Pending.label
            )
        }
        val pageInfo =
            PageInfo(list)
        var next: String? = null
        val last = "/tasks?page=" + pageInfo.pages
        if (pageInfo.pageNum < pageInfo.pages) {
            next = "/tasks?page=" + (pageInfo.pageNum + 1)
        }
        val linkVO = LinkVO(next, last)
        return AllPaymentInfoVO(
            pageInfo.total.toInt(), linkVO, pageInfo.list
        )
    }

    @Transactional(rollbackFor = [RuntimeException::class, Exception::class])
    @Throws(IOException::class, URISyntaxException::class)
    override fun findPurchase(id: String, uid: String, userAgent: String): PurchasePO {
        //不为null的时候需要先去数据库查看当前订单状态，如果当前订单状态是pending时，就去查询一遍微信当前订单状态。
        val findPurchase = paymentMapper.getPurchaseById(id, uid)
            ?: throw NotFoundException("当前订单未找到，或当前订单不属于该用户！")

        //如果订单状态为Finished、Closed,Cancelled状态，则直接返回结果
        if (findPurchase.status == PurchaseEnum.Status.Finished.label || findPurchase.status == PurchaseEnum.Status.Closed.label || findPurchase.status == PurchaseEnum.Status.Canceled.label) {
            return findPurchase
        }
        //代码如果执行到这里，证明订单状态是Pending，那么需要到微信中查询订单状态是否改变
        val (_, _, _, _, _, _, _, _, _, tradeState, _, _, transactionId) = transactionService.getOutTradeNo(id)
        var order: OrderResult? = null
        if (tradeState == "SUCCESS" || tradeState == "REFUND") {
            order = transactionService.getOrder(transactionId)
        }

        //如果订单为空，那么用户还没有支付，还是返回之前数据库的存储的状态
        if (null == order) {
            return findPurchase
        }

//        OrderResult order = new OrderResult(new AmountResult("CNY", "CNY", 100, 100), "", "", "", "", "", null, new ArrayList(), null, "SUCCESS", "", "", "", null);
        //如果状态为成功，那么需要将数据库中的状态进行更新
        if ("SUCCESS" == order.tradeState) {
            val metaJson = generateMetaInfo(uid, findPurchase.skuId, userAgent)
            generateOrderRecord(findPurchase.payments[0], findPurchase.id, uid, metaJson)
        } else if ("REFUND" == order.tradeState) {
            //修改订单状态为refunded
            purchaseMapper.updatePurchaseStatusById(id, PurchaseEnum.Status.Refunded.label)
        } else if ("USERPAYING" == order.tradeState) {
            //到这里证明用户扫码了，正在支付中
        }
        return paymentMapper.getPurchaseById(id, uid)
    }

    /**
     * 订单下单成功的状态
     *
     * @param paymentId
     * @param purchaseId
     * @param uid
     */
    private fun generateOrderRecord(paymentId: String, purchaseId: String, uid: String, metaJson: String) {
        //1.调用微信或者阿里支付查看订单状态
        val (id, _, _, cash, _, status) = paymentMapper.getPaymentById(paymentId, uid)
        //先判断资金表中当前数据状态，如果当前数据状态已然是succeed，那么直接退出
        if (status == PaymentEnum.Status.Succeed.label) {
            return
        }
        //2.如果订单已完成则更新数据库中payment表状态为完成
        val result = paymentMapper.updatePaymentStatusByIdAndUid(paymentId, uid, PaymentEnum.Status.Succeed.label)
        //如果更新状态为0，那么可能这条数据不是这个uid的，也可能订单id没找到，这时候就需要回滚并报错
        if (result == 0) {
            throw FailException("订单有误，或订单不属于当前用户！")
        }

        //3 先获取订单表信息
        val purchaseInfo = paymentMapper.getPurchaseById(purchaseId, uid)
        //4 通过订单表获取sku信息
        val (_, _, _, _, _, _, _, _, _, points) = pointMapper.getPointPriceBySkuId(purchaseInfo.skuId)
            ?: throw NotFoundException("查询的套餐未存在！")

        //5.同时添加一条点数增加金额减少的payment表记录
        //资金表信息
        val newRecodePaymentId = "pay-" + SnowflakeUtil.nextId()
        val paymentInfo = PaymentPO(
            newRecodePaymentId, uid, PaymentEnum.Type.Redeem.label, -cash, points!!, PaymentEnum.Status.Succeed.label,
            PaymentEnum.Channel.WeChat.label, purchaseId, id, metaJson, null, null, Date(), null, null
        )
        paymentMapper.addPayment(paymentInfo)

        //6.同时修改purchase表中product_id为上条添加记录的id
        paymentMapper.updatePurchaseStatusAndProductIdByid(
            purchaseId,
            uid,
            PurchaseEnum.Status.Finished.label,
            newRecodePaymentId
        )
    }

    @Throws(JsonProcessingException::class)
    private fun generateMetaInfo(uid: String, skuId: String?, userAgent: String): String {
        val userSnapshot = userInfoMapper.findOne(uid)
        var date: Date? = null
        if (null != skuId) {
            //取时间
            date = Date()
            val calendar: Calendar = GregorianCalendar()
            calendar.time = date
            //把日期往后增10天,整数  往后推,负数往前移动
            calendar.add(Calendar.DATE, pointMapper.getPointExpiredAtById(skuId))
            //这个时间就是日期往后推一天的结果
            date = calendar.time
        }
        val meta = Meta(userAgent, userSnapshot, date)
        return objectMapper.writeValueAsString(meta)
    }

    /**
     * 退款
     *
     * @param purchaseId
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    @Transactional(rollbackFor = [RuntimeException::class, Exception::class])
    @Throws(URISyntaxException::class, IOException::class)
    override fun refund(purchaseId: String, userAgent: String): PurchasePO {
        //1. 根据订单id查询订单信息
        val purchaseInfo = purchaseMapper.findByPurchaseId(purchaseId)
        //2. 通过查询到的订单信息得到订单表的id，通过订单表id查询订单表关联的数据
        val purchaseList = purchaseMapper.findById(purchaseId)
        //3. 查到关联数据后，循环判断是否存在pending状态的数据
        val existsPendingPurchase =
            purchaseList.stream().anyMatch { item: PurchasePO -> item.status == PurchaseEnum.Status.Pending.label }
        //4. 存在pending状态的记录不允许退款
        if (existsPendingPurchase) {
            throw FailException("存在以当前订单点数为标的的任务订单未完成，无法进行退款操作！")
        }
        //5. 不存在时，合并计算所有点数记录
        val (_, _, _, _, totalPoint) = paymentMapper.findPaymentByPaymentId(purchaseInfo.productId)
        //获得当前订单的总点数（充值之后得到的总点数，抛出赠送部分）
        //6. 根据第一步查询到的数据，去查询金额表记录，得到支付的金额和点数记录
        val paymentInfoList = paymentMapper.listPayment(purchaseInfo.productId)
        //得到所有消费的点数合计
        val totalSpendPoint =
            Math.abs(paymentInfoList.stream().map(PaymentPO::point).reduce { a: Int, b: Int -> Integer.sum(a, b) }
                .orElse(0))
        //剩余点数总计
        val remainAmount = totalPoint - totalSpendPoint
        if (remainAmount < 0) {
            throw FailException("计算剩余点数时错误，程序计算错误！(总点数竟小于剩余点数)")
        }
        //7. 得到点数和金额数据后，通过比例进行退款
        val (_, _, _, cash) = paymentMapper.findPaymentByPaymentId(purchaseInfo.payments[0])
        // 计算公式  (可退点数余额/订单总点数)/订单金额
        val remainPoint = totalPoint - totalSpendPoint
        val remainCash = remainPoint / totalPoint * cash

        //9.在退款之前先将要退款的订单的状态变更为refund
        purchaseMapper.updatePurchaseStatusById(purchaseInfo.id, PurchaseEnum.Status.Refunding.label)
        paymentMapper.updatePaymentStatusById(purchaseInfo.payments[0], PaymentEnum.Status.Refunding.label)
        paymentMapper.updatePaymentStatusById(purchaseInfo.productId, PaymentEnum.Status.Refunding.label)
        //8. 微信退款
        wechatRefund(purchaseInfo, remainPoint, remainCash, userAgent)
        //11. 返回订单新状态
        return purchaseMapper.findByPurchaseId(purchaseId)
    }

    /**
     * 微信退款
     *
     * @return
     */
    @Throws(URISyntaxException::class, IOException::class)
    private fun wechatRefund(purchaseInfo: PurchasePO, remainPoint: Int, remainCash: Int, userAgent: String) {
        //8. 调用微信退款接口进行退款
        val (_, _, _, _, _, _, _, _, _, tradeState, _, _, transactionId) = transactionService.getOutTradeNo(purchaseInfo.id)
        if (tradeState != "SUCCESS") {
            throw FailException("订单发生异常，请联系管理员！")
        }
        val (_, _, _, _, _, _, _, _, _, _, _, _, transactionId1) = transactionService.getOrder(transactionId)
        if (null == transactionId1) {
            throw FailException("微信订单号为空！")
        }
        val (_, _, _, cash) = paymentMapper.findPaymentByPaymentId(purchaseInfo.payments[0])
        val (_, _, _, _, _, _, _, _, _, status) = transactionService.applyRefunds(
            transactionId1,
            purchaseInfo.id,
            "system",
            remainCash,
            cash
        )

        //9. 退款如果返回处理中，则返回206状态
        //10. 如果返回退款已关闭，则返回410状态
        //11. 如果返回异常则返回500状态
        if (status == "RESOURCE_NOT_EXISTS") {
            throw FailException("请检查退款单号是否有误以及订单状态是否正确，如：未支付、已支付未退款")
        } else if (status == "SIGN_ERROR") {
            throw FailException("MCH_NOT_EXISTS")
        } else if (status == "SIGN_ERROR") {
            throw FailException("请检查MCHID是否正确")
        } else if (status == "PARAM_ERROR") {
            throw FailException("请求参数错误，请检查参数再调用退款查询")
        } else if (status == "SUCCESS") {
            //成功退款
            purchaseMapper.updatePurchaseStatusById(purchaseInfo.id, PurchaseEnum.Status.Refunded.label)
            paymentMapper.updatePaymentStatusById(purchaseInfo.payments[0], PaymentEnum.Status.Refunded.label)
            paymentMapper.updatePaymentStatusById(purchaseInfo.productId, PaymentEnum.Status.Refunded.label)

            //资金表id
            val paymentId = "pay-" + SnowflakeUtil.nextId()
            //订单表id
            val paymentId1 = "pay-" + SnowflakeUtil.nextId()
            val metaJson = generateMetaInfo(purchaseInfo.uid, null, userAgent)
            //资金表信息
            val paymentPO = PaymentPO(
                paymentId,
                purchaseInfo.uid,
                PaymentEnum.Type.Redeem.label,
                remainCash,
                -remainPoint,
                PaymentEnum.Status.Refunded.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseInfo.id,
                null,
                metaJson,
                null,
                null,
                null,
                null,
                null
            )
            paymentMapper.addPayment(paymentPO)
            val paymentPO1 = PaymentPO(
                paymentId1,
                purchaseInfo.uid,
                PaymentEnum.Type.Refund.label,
                -remainCash,
                remainPoint,
                PaymentEnum.Status.Refunded.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseInfo.id,
                null,
                metaJson,
                null,
                null,
                null,
                null,
                null
            )
            paymentMapper.addPayment(paymentPO1)
        } else if (status == "PROCESSING") {
            // 正在退款
            purchaseMapper.updatePurchaseStatusById(purchaseInfo.id, PurchaseEnum.Status.Refunded.label)
            paymentMapper.updatePaymentStatusById(purchaseInfo.payments[0], PaymentEnum.Status.Refunded.label)
            paymentMapper.updatePaymentStatusById(purchaseInfo.productId, PaymentEnum.Status.Refunded.label)

            //资金表id
            val paymentId = "pay-" + SnowflakeUtil.nextId()
            //订单表id
            val paymentId1 = "pay-" + SnowflakeUtil.nextId()
            val metaJson = generateMetaInfo(purchaseInfo.uid, null, userAgent)
            //资金表信息
            val paymentPO = PaymentPO(
                paymentId,
                purchaseInfo.uid,
                PaymentEnum.Type.Redeem.label,
                remainCash,
                -remainPoint,
                PaymentEnum.Status.Refunded.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseInfo.id,
                null,
                metaJson,
                null,
                null,
                null,
                null,
                null
            )
            paymentMapper.addPayment(paymentPO)
            val paymentPO1 = PaymentPO(
                paymentId1,
                purchaseInfo.uid,
                PaymentEnum.Type.Refund.label,
                -remainCash,
                0,
                PaymentEnum.Status.Refunded.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseInfo.id,
                null,
                metaJson,
                null,
                null,
                null,
                null,
                null
            )
            paymentMapper.addPayment(paymentPO1)
        } else {
            throw FailException("其他未知错误！")
        }
    }

    /**
     * 商品购买接口
     *
     * @param uid
     * @param userAgent
     * @param skuInfo
     * @param amount
     * @param purchaseId
     * @param paymentId
     * @param metaJson
     * @return
     */
    @Transactional(rollbackFor = [RuntimeException::class, Exception::class])
    override fun purchase(
        uid: String,
        userAgent: String,
        skuInfo: SkuPO,
        amount: Int,
        purchaseId: String,
        paymentId: String,
        metaJson: String
    ): PurchasePO {
        //3.2 如果类型是点数充值，则需要需要生成一条订单信息，两条资金表记录(一条资金进账，另一条资金转点数记录)
        //订单表信息
        val purchasePO = PurchasePO(
            purchaseId,
            uid,
            null, skuInfo.id,
            amount,
            0,
            paymentId,
            PurchaseEnum.Status.Finished.label,
            null,
            null,
            null
        )
        paymentMapper.addPurchase(purchasePO)
        //资金表信息
        val pointCount = skuInfo.points!!.times(amount)
        val paymentPO = PaymentPO(
            paymentId,
            uid,
            PaymentEnum.Type.Redeem.label,
            skuInfo.price!!,
            pointCount,
            PaymentEnum.Status.Succeed.label,
            PaymentEnum.Channel.WeChat.label,
            purchaseId,
            null,
            metaJson,
            null,
            null,
            null,
            null,
            null
        )
        paymentMapper.addPayment(paymentPO)
        return paymentMapper.getPurchaseById(purchaseId, uid)
    }

    override fun evaluation(uid: String, taskId: String, skuId: String, amount: Int): PurchaseMeta {
        //订单表id
        val purchaseId = "pur-" + SnowflakeUtil.nextId()
        //1.根据skuId查询
        val purchasePO = PurchasePO(
            purchaseId,
            uid,
            null,
            skuId,
            amount,
            0,
            taskId,
            PurchaseEnum.Status.Pending.label,
            null,
            null,
            null
        )
        paymentMapper.addPurchase(purchasePO)
        val neededPoint = calculateSkuAmountAggregate(skuId, amount)
        return PurchaseMeta(purchaseId, neededPoint, taskId)
    }

    /**
     * 费用计算
     */
    @Transactional(rollbackFor = [RuntimeException::class, Exception::class])
    override fun judgeCost(uid: String, taskId: String, purchaseId: String): String {
        var purchasePackDoubleList: PurchasePackDoubleList? = null
        val envelopNeedPoint = IntArray(1)

        //获取用户最新的点数消费记录
        val lastCurrentUserCostRecord = paymentMapper.getLastCurrentUserCostRecord(uid, PaymentEnum.Type.Spending.label)

        //获取订单信息
        val purchaseInfo = purchaseMapper.findByPurchaseId(purchaseId)
        //订单表id
        val payments: MutableList<String> = ArrayList()

        //如果用户没有消费，第一次进行消费
        if (null == lastCurrentUserCostRecord) {
            //那么就去检查所有记录
            purchasePackDoubleList = getUserLastWalletInfo(uid)
            //得到这次执行任务需要的点数
            val neededPoint = calculateSkuAmountAggregate(purchaseInfo.skuId, purchaseInfo.amount)
            envelopNeedPoint[0] = neededPoint
            if (purchasePackDoubleList.length == 0) {
                throw FailException("未发现有可用的钱包！")
            }
            recordNeededPoint(
                uid,
                purchaseId,
                payments,
                purchasePackDoubleList.getFirst(),
                envelopNeedPoint,
                purchasePackDoubleList
            )
        } else {
            //如果有消费记录，获取消费记录下，是否存在点数增加的数据
            val paymentPOS = paymentMapper.listLastUserRechargeRecord(uid, lastCurrentUserCostRecord.createdAt)
            paymentPOS.add(lastCurrentUserCostRecord)
            //那么就获取能用的最老的钱包
            purchasePackDoubleList = getUserLastWalletInfo(uid, paymentPOS)

            //得到这次执行任务需要的点数
            val neededPoint = calculateSkuAmountAggregate(purchaseInfo.skuId, purchaseInfo.amount)
            envelopNeedPoint[0] = neededPoint
            if (purchasePackDoubleList.length == 0) {
                throw FailException("未发现有可用的钱包！")
            }
            try {
                recordNeededPoint(
                    uid,
                    purchaseId,
                    payments,
                    purchasePackDoubleList.getFirst(),
                    envelopNeedPoint,
                    purchasePackDoubleList
                )
            } catch (e: FailException) {
                //如果是因为点数不够失败，应该到用户所有子钱包中全量
                purchasePackDoubleList = getUserLastWalletInfo(uid)
                recordNeededPoint(
                    uid,
                    purchaseId,
                    payments,
                    purchasePackDoubleList.getFirst(),
                    envelopNeedPoint,
                    purchasePackDoubleList
                )
            }
        }
        if (purchasePackDoubleList.getLength() == 0) {
            throw FailException("当前用户无点数可用！")
        }
        return purchaseId
    }

    /**
     * 计算任务需要的费(递归)
     *
     * @param uid
     * @param purchasePackInfoVO
     * @param envelopNeedPoint
     * @param purchasePackDoubleList
     */
    private fun recordNeededPoint(
        uid: String,
        purchaseId: String,
        payments: MutableList<String>,
        purchasePackInfoVO: PurchasePackInfoVO?,
        envelopNeedPoint: IntArray,
        purchasePackDoubleList: PurchasePackDoubleList?
    ) {
        if (null == purchasePackInfoVO) {
            throw FailException("已经不够点数来扣费了！还需要" + envelopNeedPoint[0] + "点")
        }

        //得到剩余点数
        val remainPoint = calculateAppointWalletRemain(uid, purchasePackInfoVO.productId)
        //如果剩余点数为0，证明当前钱包已经没有钱，快速换下一个
        if (remainPoint == 0) {
            recordNeededPoint(
                uid,
                purchaseId,
                payments,
                purchasePackDoubleList!!.getNext(purchasePackInfoVO),
                envelopNeedPoint,
                purchasePackDoubleList
            )
        } else if (remainPoint + envelopNeedPoint[0] < 0) {
            //如果不够扣的，需要下一个点数套餐来扣
            //如果不够，分段扣费
            val newRecodePaymentId = "pay-" + SnowflakeUtil.nextId()
            payments.add(newRecodePaymentId)
            val paymentInfo = PaymentPO(
                newRecodePaymentId,
                uid,
                PaymentEnum.Type.Spending.label,
                0,
                -remainPoint,
                PaymentEnum.Status.Succeed.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseId,
                null,
                null,
                null,
                null,
                Date(),
                purchasePackInfoVO.productId,
                purchasePackInfoVO.productId
            )
            paymentMapper.addPayment(paymentInfo)
            envelopNeedPoint[0] = envelopNeedPoint[0] + remainPoint
            //分段记录
            recordNeededPoint(
                uid,
                purchaseId,
                payments,
                purchasePackDoubleList!!.getNext(purchasePackInfoVO),
                envelopNeedPoint,
                purchasePackDoubleList
            )
        } else {
            //如果够扣直接记录扣款
            val newRecodePaymentId = "pay-" + SnowflakeUtil.nextId()
            payments.add(newRecodePaymentId)
            val paymentInfo = PaymentPO(
                newRecodePaymentId,
                uid,
                PaymentEnum.Type.Spending.label,
                0,
                envelopNeedPoint[0],
                PaymentEnum.Status.Succeed.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseId,
                null,
                null,
                null,
                null,
                Date(),
                purchasePackInfoVO.productId,
                purchasePackInfoVO.productId
            )
            paymentMapper.addPayment(paymentInfo)
            purchaseMapper.updatePurchasePaymentsById(purchaseId,  payments.toTypedArray())
        }
    }

    /**
     * 获取久远的钱包信息(用于用户已经有消费记录的情况)
     *
     * @param uid
     * @param paymentPOList
     * @return
     */
    private fun getUserLastWalletInfo(
        uid: String,
        paymentPOList: @NotNull MutableList<PaymentPO>?
    ): PurchasePackDoubleList {
        val purchasePackDoubleList = PurchasePackDoubleList()
        paymentPOList!!.forEach(Consumer { (_, _, _, _, _, _, _, _, _, _, _, _, _, _, slot): PaymentPO ->
            if (null != slot) {
                val (_, _, _, _, _, _, _, purchaseId) = paymentMapper.getPaymentById(slot, uid)
                val userPurchasePointPack = purchaseMapper.getUserPurchasePointPack(uid, purchaseId)
                if (null == userPurchasePointPack) {
                    //这里需要记录一下为什么为null，需要打印日志
                } else {
                    purchasePackDoubleList.dynamicAdd(userPurchasePointPack)
                }
            }
        })
        return if (purchasePackDoubleList.getLength() == 0) {
            getUserLastWalletInfo(uid)
        } else purchasePackDoubleList
    }

    /**
     * 计算最老的钱包 (双向链表、递归，将点数放到合适的位置)(用于用户初次消费的情况)
     */
    private fun getUserLastWalletInfo(uid: String): PurchasePackDoubleList {
        val purchasePackDoubleList = PurchasePackDoubleList()

        //得到赠送的点数记录
        var purchasePackInfoVOList = purchaseMapper.listUserPurchasePointPack(uid, SkuEnum.Type.PointGift.label)
        //如果赠送的点数记录是空，那么需要对有序的用户钱包集合做初始化
        purchasePackInfoVOList?.forEach(Consumer { o: PurchasePackInfoVO? -> purchasePackDoubleList.dynamicAdd(o) })
        purchasePackInfoVOList = purchaseMapper.listUserPurchasePointPack(uid, SkuEnum.Type.PointPack.label)
        purchasePackInfoVOList?.forEach(Consumer { o: PurchasePackInfoVO? -> purchasePackDoubleList.dynamicAdd(o) })
        return purchasePackDoubleList
    }

    /**
     * 计算指定钱包的剩余点数
     *
     * @param uid
     * @param paymentId
     */
    private fun calculateAppointWalletRemain(uid: String, paymentId: String): Int {
        val (_, _, _, _, point) = paymentMapper.getPaymentById(paymentId, uid)
        val remainPoint = paymentMapper.countPaymentByUidAndSlot(uid, paymentId)
        val resultPoint = point + remainPoint
        if (resultPoint < 0) {
            throw FailException("点数计算出错，计算到的点数出现负数！")
        }
        return resultPoint
    }

    /**
     * 计算某个sku和数量下，应花费的点数总计
     *
     * @param skuId
     * @param amount
     * @return
     */
    private fun calculateSkuAmountAggregate(skuId: String, amount: Int): Int {
        if (amount <= 0) {
            throw FailException("要执行的数量不能小于等于0")
        }
        val (_, _, _, _, _, _, _, _, _, points) = pointMapper.getPointPriceBySkuId(skuId)
            ?: throw NotFoundException("没有发现id为" + skuId + "的sku信息")
        return points!! * amount
    }

    /**
     * 退点
     *
     * @param uid
     * @param taskId
     * @param amount 要退的点数
     */
    override fun refundPoint(uid: String, taskId: String, amount: Int) {
        //1. 通过taskId得到订单信息
        var amount = amount
        val purchaseInfo = purchaseMapper.findByPurchaseByProductId(taskId) ?: throw FailException("未发现与当前任务有关的订单")
        //2.通过流水记录找到用户的钱包
        val paymentPOS = paymentMapper.listPayment(purchaseInfo.id)
        //3.找到子钱包后，计算钱包哪一个比较新，将返还的点数放回钱包中
        val userNewestWalletInfo = getUserLastWalletInfo(uid, paymentPOS)
        //4. 由于代码复用，因此得到的钱包最老的在第一个，要找最新的子钱包需要得到last值，倒推
        val purchasePackInfoVO = userNewestWalletInfo.getLast()
        //5.得到sku执行需要的点数信息
        amount = calculateSkuAmountAggregate(purchaseInfo.skuId, amount)
        val envelopPoint = IntArray(1)
        envelopPoint[0] = Math.abs(amount)
        repoint(uid, purchaseInfo.id, envelopPoint, purchasePackInfoVO, userNewestWalletInfo)
    }

    /**
     * 退点
     *
     * @param uid
     * @param purchaseId
     * @param envelopPoint
     * @param purchasePackInfoVO
     * @param userNewestWalletInfo
     */
    fun repoint(
        uid: String?,
        purchaseId: String?,
        envelopPoint: IntArray,
        purchasePackInfoVO: PurchasePackInfoVO,
        userNewestWalletInfo: PurchasePackDoubleList
    ) {
        //5. 得到钱包了，要先计算一下钱包总点数是多少
        val (id) = paymentMapper.getPaymentById(purchasePackInfoVO.productId, uid)
        //6. 得到用了多少点
        val remainPoint = paymentMapper.countPaymentByUidAndSlot(uid, purchasePackInfoVO.productId)

        //如果这个钱包没有消费，证明根本无法再退点进去，直接找下一个钱包
        if (remainPoint == 0) {
            repoint(
                uid,
                purchaseId,
                envelopPoint,
                userNewestWalletInfo.getPrevious(purchasePackInfoVO),
                userNewestWalletInfo
            )
        } else if (Math.abs(remainPoint) < envelopPoint[0]) {
            envelopPoint[0] = envelopPoint[0] + remainPoint
            //证明一个子钱包退款装不下，需要再装一部分到其他子钱包中
            val newRecodePaymentId = "pay-" + SnowflakeUtil.nextId()
            val paymentInfo = PaymentPO(
                newRecodePaymentId,
                uid!!,
                PaymentEnum.Type.Refund.label,
                0,
                Math.abs(remainPoint),
                PaymentEnum.Status.Succeed.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseId!!,
                null,
                null,
                null,
                null,
                Date(),
                id,
                purchasePackInfoVO.productId
            )
            paymentMapper.addPayment(paymentInfo)
            repoint(
                uid,
                purchaseId,
                envelopPoint,
                userNewestWalletInfo.getPrevious(purchasePackInfoVO),
                userNewestWalletInfo
            )
        } else {
            val newRecodePaymentId = "pay-" + SnowflakeUtil.nextId()
            val paymentInfo = PaymentPO(
                newRecodePaymentId,
                uid!!,
                PaymentEnum.Type.Refund.label,
                0,
                envelopPoint[0],
                PaymentEnum.Status.Succeed.label,
                PaymentEnum.Channel.WeChat.label,
                purchaseId!!,
                null,
                null,
                null,
                null,
                Date(),
                id,
                purchasePackInfoVO.productId
            )
            paymentMapper.addPayment(paymentInfo)
        }
    }
}