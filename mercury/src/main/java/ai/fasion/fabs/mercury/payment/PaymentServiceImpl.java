package ai.fasion.fabs.mercury.payment;

import ai.fasion.fabs.mercury.domain.PageRequest;
import ai.fasion.fabs.mercury.payment.po.PaymentPO;
import ai.fasion.fabs.mercury.payment.po.PurchasePackInfoVO;
import ai.fasion.fabs.mercury.payment.pojo.ChannelInfo;
import ai.fasion.fabs.mercury.payment.pojo.PaymentInfo;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseInfo;
import ai.fasion.fabs.mercury.payment.pojo.PurchaseMeta;
import ai.fasion.fabs.mercury.payment.vo.*;
import ai.fasion.fabs.mercury.point.PointMapper;
import ai.fasion.fabs.mercury.point.po.SkuPO;
import ai.fasion.fabs.mercury.payment.po.PurchasePO;
import ai.fasion.fabs.mercury.profile.UserInfoMapper;
import ai.fasion.fabs.mercury.wechat.TransactionService;
import ai.fasion.fabs.mercury.wechat.pojo.OrderInfo;
import ai.fasion.fabs.mercury.wechat.pojo.OrderResult;
import ai.fasion.fabs.mercury.wechat.pojo.RefundResult;
import ai.fasion.fabs.vesta.enums.PaymentEnum;
import ai.fasion.fabs.vesta.enums.PurchaseEnum;
import ai.fasion.fabs.vesta.enums.SkuEnum;
import ai.fasion.fabs.vesta.expansion.AuthorizationException;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.expansion.ForbiddenException;
import ai.fasion.fabs.vesta.expansion.NotFoundException;
import ai.fasion.fabs.vesta.utils.SnowflakeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Function:
 *
 * @author miluo
 * Date: 2021/8/16 17:25
 * @since JDK 1.8
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PointMapper pointMapper;
    private final TransactionService transactionService;
    private final UserInfoMapper userInfoMapper;
    private final ObjectMapper objectMapper;
    private final PurchaseMapper purchaseMapper;

    public PaymentServiceImpl(PaymentMapper paymentMapper, PointMapper pointMapper, UserInfoMapper userInfoMapper, ObjectMapper objectMapper, TransactionService transactionService, PurchaseMapper purchaseMapper) {
        this.paymentMapper = paymentMapper;
        this.pointMapper = pointMapper;
        this.userInfoMapper = userInfoMapper;
        this.objectMapper = objectMapper;
        this.transactionService = transactionService;
        this.purchaseMapper = purchaseMapper;
    }


    /**
     * 根据uid获取用户下所有支付信息
     *
     * @param pageRequest
     * @param uid
     * @return
     */
    @Override
    public AllPaymentInfoVO findAllByUid(PageRequest pageRequest, String uid, String type) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        PageInfo<PaymentVO> pageInfo = new PageInfo<>(paymentMapper.selectPayments(uid, type));
        String next = null;
        String last = "/tasks?page=" + pageInfo.getPages();
        if (pageInfo.getPageNum() < pageInfo.getPages()) {
            next = "/tasks?page=" + (pageInfo.getPageNum() + 1);
        }
        LinkVO linkVO = new LinkVO(next, last);
        AllPaymentInfoVO allPaymentInfoVO = new AllPaymentInfoVO((int) pageInfo.getTotal(), linkVO, pageInfo.getList());
        return allPaymentInfoVO;
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
    @Override
    public PurchaseInfo createOrder(String uid, String skuId, String channel, Integer amount, String userAgent) throws IOException {
        //1.根据sku查询sku套餐信息的金额
        SkuPO currentPointInfo = pointMapper.getPointPriceBySkuId(skuId);
        if (null == currentPointInfo) {
            throw new NotFoundException("没有找到对应的套餐信息！");
        }
        //2. 判断sku的状态是否是启用的状态，如果不是，也不允许操作
        if (Objects.equals(currentPointInfo.getStatus(), SkuEnum.Status.Disable.getLabel())) {
            throw new FailException("套餐没有启动，无法进行消费！");
        }

        //3.insert data in payment table and purchase table
        //资金表id
        String paymentId = "pay-" + SnowflakeUtil.nextId();
        //订单表id
        String purchaseId = "pur-" + SnowflakeUtil.nextId();

        //3. 判断sku是支付还是执行
        String qrCodeUrl = null;
        String orderId = null;
        String paymentIds = null;
        PurchasePO purchasePO = new PurchasePO();
        if (SkuEnum.Type.PointPack.getLabel().equals(currentPointInfo.getType())) {
            String metaJson = generateMetaInfo(uid, null, userAgent);
            PayMentInfoVO payMentInfoVO = getQrCodeUrl(uid, currentPointInfo, PaymentEnum.Channel.channelOf(channel), purchaseId, paymentId, metaJson);
            orderId = payMentInfoVO.getOrderId();
            qrCodeUrl = payMentInfoVO.getQrCodeUrl();
            //通过订单id和uid查询
            purchasePO = paymentMapper.getPurchaseById(orderId, uid);
            paymentIds = purchasePO.getPayments()[0];
        } else {
            String metaJson = generateMetaInfo(uid, currentPointInfo.getId(), userAgent);
            PurchasePO purchase = purchase(uid, userAgent, currentPointInfo, amount, purchaseId, paymentId, metaJson);
            qrCodeUrl = null;
            orderId = purchase.getId();
            purchasePO = paymentMapper.getPurchaseById(orderId, uid);
            paymentIds = purchasePO.getProductId();
        }
        //找到paymentId查询payment表
        PaymentPO paymentPO = paymentMapper.findPaymentByPaymentId(paymentIds);
        //ChannelInfo类型赋值
        ChannelInfo channelInfo = new ChannelInfo(paymentPO.getChannel(), qrCodeUrl);
        //paymentInfo类型赋值
        PaymentInfo paymentInfo = new PaymentInfo(paymentPO.getId(), paymentPO.getType(), paymentPO.getPoint(), channelInfo, paymentPO.getStatus());
        //purchaseInfo类型赋值
        PurchaseInfo purchaseInfo = new PurchaseInfo(purchasePO.getId(), purchasePO.getUid(), paymentInfo, purchasePO.getSkuId(), purchasePO.getAmount(),
                purchasePO.getShipped(), purchasePO.getProductId(), purchasePO.getStatus(), purchasePO.getCreatedAt(), purchasePO.getUpdatedAt(), purchasePO.getFinishedAt());

        return purchaseInfo;
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
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public PayMentInfoVO getQrCodeUrl(String uid, SkuPO skuInfo, PaymentEnum.Channel channel, String purchaseId, String paymentId, String metaJson) throws IOException {

        String[] payments = {paymentId};
        //订单表信息
        PurchasePO
                purchasePO = new PurchasePO(purchaseId, uid, payments, Objects.requireNonNull(skuInfo.getId()), 1, 0, null, PurchaseEnum.Status.Pending.getLabel(), null, null, null);
        paymentMapper.addPurchase(purchasePO);
        //资金表信息
        PaymentPO paymentPO = new PaymentPO(paymentId, uid, PaymentEnum.Type.Recharge.getLabel(), skuInfo.getPrice(), 0, PaymentEnum.Status.Pending.getLabel(),
                PaymentEnum.Channel.WeChat.getLabel(), purchaseId, null, metaJson, null, null, null, null, null);
        paymentMapper.addPayment(paymentPO);

        //2.根据channel判断调起哪个支付平台(Alipay、Wechat)
        if (PaymentEnum.Channel.WeChat.equals(channel)) {
            //4.invoke Wechat payment return QcUrl
            String qcUrl = transactionService.createOrder(purchaseId, skuInfo.getPrice(), skuInfo.getSlogan(), "system");
            return new PayMentInfoVO(purchaseId, qcUrl);
        } else if (PaymentEnum.Channel.Alipay.equals(channel)) {
            return null;
        } else {
            throw new NotFoundException("没有找到匹配的渠道！");
        }
    }

    @Override
    public AllPaymentInfoVO purchaseList(String uid, PageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<Object> list = new ArrayList<>();
        //查询点数套餐(point-pack)订单下的数据
        List<PurchaseVO> pointPackList = paymentMapper.purchaseList(PurchaseEnum.Type.PointPack.getCode(), uid, PaymentEnum.Status.Pending.getLabel());
        List<PurchaseVO> faceSwapList = paymentMapper.purchaseTypeList(PurchaseEnum.Type.FaceSwap.getCode(), uid, PaymentEnum.Status.Pending.getLabel());
        List<PurchaseVO> mattingImageList = paymentMapper.purchaseTypeList(PurchaseEnum.Type.MattingImage.getCode(), uid, PaymentEnum.Status.Pending.getLabel());
        list.addAll(pointPackList);
        list.addAll(faceSwapList);
        list.addAll(mattingImageList);
        PageInfo<Object> pageInfo = new PageInfo<>(list);
        String next = null;
        String last = "/tasks?page=" + pageInfo.getPages();
        if (pageInfo.getPageNum() < pageInfo.getPages()) {
            next = "/tasks?page=" + (pageInfo.getPageNum() + 1);
        }
        LinkVO linkVO = new LinkVO(next, last);
        AllPaymentInfoVO allPaymentInfoVO = new AllPaymentInfoVO((int) pageInfo.getTotal(), linkVO, pageInfo.getList());
        return allPaymentInfoVO;
    }

    @Override
    public AllPaymentInfoVO purchaseListByType(String type, String uid, PageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<PurchaseVO> list = null;
        if (PurchaseEnum.Type.PointPack.getLabel().equals(type)) {
            //查询点数套餐(point-pack)订单下的数据
            list = paymentMapper.purchaseList(PurchaseEnum.Type.PointPack.getCode(), uid, PaymentEnum.Status.Pending.getLabel());
        } else if (PurchaseEnum.Type.FaceSwap.getLabel().equals(type)) {
            list = paymentMapper.purchaseTypeList(PurchaseEnum.Type.FaceSwap.getCode(), uid, PaymentEnum.Status.Pending.getLabel());
        } else if (PurchaseEnum.Type.MattingImage.getLabel().equals(type)) {
            list = paymentMapper.purchaseTypeList(PurchaseEnum.Type.MattingImage.getCode(), uid, PaymentEnum.Status.Pending.getLabel());
        }else if (PurchaseEnum.Type.PointGift.getLabel().equals(type)) {
            list = paymentMapper.purchaseTypeList(PurchaseEnum.Type.PointGift.getCode(), uid, PaymentEnum.Status.Pending.getLabel());
        }  else {
            throw new NotFoundException("传入的订单类型有误！");
        }
        PageInfo<PurchaseVO> pageInfo = new PageInfo<>(list);
        String next = null;
        String last = "/tasks?page=" + pageInfo.getPages();
        if (pageInfo.getPageNum() < pageInfo.getPages()) {
            next = "/tasks?page=" + (pageInfo.getPageNum() + 1);
        }
        LinkVO linkVO = new LinkVO(next, last);
        AllPaymentInfoVO allPaymentInfoVO = new AllPaymentInfoVO((int) pageInfo.getTotal(), linkVO, pageInfo.getList());
        return allPaymentInfoVO;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public PurchasePO findPurchase(String id, String uid, String userAgent) throws IOException, URISyntaxException {
        //不为null的时候需要先去数据库查看当前订单状态，如果当前订单状态是pending时，就去查询一遍微信当前订单状态。
        PurchasePO findPurchase = paymentMapper.getPurchaseById(id, uid);
        if (null == findPurchase) {
            throw new NotFoundException("当前订单未找到，或当前订单不属于该用户！");
        }

        //如果订单状态为Finished、Closed,Cancelled状态，则直接返回结果
        if (findPurchase.getStatus().equals(PurchaseEnum.Status.Finished.getLabel())
                || findPurchase.getStatus().equals(PurchaseEnum.Status.Closed.getLabel())
                || findPurchase.getStatus().equals(PurchaseEnum.Status.Canceled.getLabel())) {
            return findPurchase;
        }
        //代码如果执行到这里，证明订单状态是Pending，那么需要到微信中查询订单状态是否改变
        OrderResult outTradeNo = transactionService.getOutTradeNo(id);
        OrderResult order = null;
        if (outTradeNo.getTradeState().equals("SUCCESS") || outTradeNo.getTradeState().equals("REFUND")) {
            order = transactionService.getOrder(outTradeNo.getTransactionId());
        }

        //如果订单为空，那么用户还没有支付，还是返回之前数据库的存储的状态
        if (null == order) {
            return findPurchase;
        }

//        OrderResult order = new OrderResult(new AmountResult("CNY", "CNY", 100, 100), "", "", "", "", "", null, new ArrayList(), null, "SUCCESS", "", "", "", null);
        //如果状态为成功，那么需要将数据库中的状态进行更新
        if ("SUCCESS".equals(order.getTradeState())) {
            String metaJson = generateMetaInfo(uid, findPurchase.getSkuId(), userAgent);
            generateOrderRecord(findPurchase.getPayments()[0], findPurchase.getId(), uid, metaJson);
        } else if (("REFUND").equals(order.getTradeState())) {
            //修改订单状态为refunded
            purchaseMapper.updatePurchaseStatusById(id, PurchaseEnum.Status.Refunded.getLabel());
        } else if ("USERPAYING".equals(order.getTradeState())) {
            //到这里证明用户扫码了，正在支付中
        }
        return paymentMapper.getPurchaseById(id, uid);
    }

    /**
     * 订单下单成功的状态
     *
     * @param paymentId
     * @param purchaseId
     * @param uid
     */
    private void generateOrderRecord(String paymentId, String purchaseId, String uid, String metaJson) {
        //1.调用微信或者阿里支付查看订单状态
        PaymentPO paymentPO = paymentMapper.getPaymentById(paymentId, uid);
        //先判断资金表中当前数据状态，如果当前数据状态已然是succeed，那么直接退出
        if (paymentPO.getStatus().equals(PaymentEnum.Status.Succeed.getLabel())) {
            return;
        }
        //2.如果订单已完成则更新数据库中payment表状态为完成
        int result = paymentMapper.updatePaymentStatusByIdAndUid(paymentId, uid, PaymentEnum.Status.Succeed.getLabel());
        //如果更新状态为0，那么可能这条数据不是这个uid的，也可能订单id没找到，这时候就需要回滚并报错
        if (result == 0) {
            throw new FailException("订单有误，或订单不属于当前用户！");
        }

        //3 先获取订单表信息
        PurchasePO purchaseInfo = paymentMapper.getPurchaseById(purchaseId, uid);
        //4 通过订单表获取sku信息
        SkuPO currentPointInfo = pointMapper.getPointPriceBySkuId(purchaseInfo.getSkuId());
        if (null == currentPointInfo) {
            throw new NotFoundException("查询的套餐未存在！");
        }

        //5.同时添加一条点数增加金额减少的payment表记录
        //资金表信息
        String newRecodePaymentId = "pay-" + SnowflakeUtil.nextId();
        PaymentPO paymentInfo = new PaymentPO(newRecodePaymentId, uid, PaymentEnum.Type.Redeem.getLabel(), -paymentPO.getCash(), currentPointInfo.getPoints(), PaymentEnum.Status.Succeed.getLabel(),
                PaymentEnum.Channel.WeChat.getLabel(), purchaseId, paymentPO.getId(), metaJson, null, null, new Date(), null, null);
        paymentMapper.addPayment(paymentInfo);

        //6.同时修改purchase表中product_id为上条添加记录的id
        paymentMapper.updatePurchaseStatusAndProductIdByid(purchaseId, uid, PurchaseEnum.Status.Finished.getLabel(), newRecodePaymentId);
    }


    private String generateMetaInfo(String uid, String skuId, String userAgent) throws JsonProcessingException {
        MetaInfo.UserSnapshot userSnapshot = userInfoMapper.findOne(uid);
        Date date = null;
        if (null != skuId) {
            //取时间
            date = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            //把日期往后增10天,整数  往后推,负数往前移动
            calendar.add(Calendar.DATE, pointMapper.getPointExpiredAtById(skuId));
            //这个时间就是日期往后推一天的结果
            date = calendar.getTime();
        }

        MetaInfo.Meta meta = new MetaInfo.Meta(userAgent, userSnapshot, date);
        return objectMapper.writeValueAsString(meta);
    }

    /**
     * 退款
     *
     * @param purchaseId
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public PurchasePO refund(String purchaseId, String userAgent) throws URISyntaxException, IOException {
        //1. 根据订单id查询订单信息
        PurchasePO purchaseInfo = purchaseMapper.findByPurchaseId(purchaseId);
        //2. 通过查询到的订单信息得到订单表的id，通过订单表id查询订单表关联的数据
        List<PurchasePO> purchaseList = purchaseMapper.findById(purchaseId);
        //3. 查到关联数据后，循环判断是否存在pending状态的数据
        boolean existsPendingPurchase = purchaseList.stream().anyMatch(item -> item.getStatus().equals(PurchaseEnum.Status.Pending.getLabel()));
        //4. 存在pending状态的记录不允许退款
        if (existsPendingPurchase) {
            throw new FailException("存在以当前订单点数为标的的任务订单未完成，无法进行退款操作！");
        }
        //5. 不存在时，合并计算所有点数记录
        PaymentPO productPaymentInfo = paymentMapper.findPaymentByPaymentId(purchaseInfo.getProductId());
        //获得当前订单的总点数（充值之后得到的总点数，抛出赠送部分）
        int totalPoint = productPaymentInfo.getPoint();
        //6. 根据第一步查询到的数据，去查询金额表记录，得到支付的金额和点数记录
        List<PaymentPO> paymentInfoList = paymentMapper.listPayment(purchaseInfo.getProductId());
        //得到所有消费的点数合计
        int totalSpendPoint = Math.abs(paymentInfoList.stream().map(PaymentPO::getPoint).reduce(Integer::sum).orElse(0));
        //剩余点数总计
        int remainAmount = totalPoint - totalSpendPoint;
        if (remainAmount < 0) {
            throw new FailException("计算剩余点数时错误，程序计算错误！(总点数竟小于剩余点数)");
        }
        //7. 得到点数和金额数据后，通过比例进行退款
        PaymentPO originPaymentInfo = paymentMapper.findPaymentByPaymentId(purchaseInfo.getPayments()[0]);
        // 计算公式  (可退点数余额/订单总点数)/订单金额
        int remainPoint = totalPoint - totalSpendPoint;
        int remainCash = (remainPoint / totalPoint) * originPaymentInfo.getCash();

        //9.在退款之前先将要退款的订单的状态变更为refund
        purchaseMapper.updatePurchaseStatusById(purchaseInfo.getId(), PurchaseEnum.Status.Refunding.getLabel());
        paymentMapper.updatePaymentStatusById(purchaseInfo.getPayments()[0], PaymentEnum.Status.Refunding.getLabel());
        paymentMapper.updatePaymentStatusById(purchaseInfo.getProductId(), PaymentEnum.Status.Refunding.getLabel());
        //8. 微信退款
        wechatRefund(purchaseInfo, remainPoint, remainCash, userAgent);
        //11. 返回订单新状态
        return purchaseMapper.findByPurchaseId(purchaseId);
    }

    /**
     * 微信退款
     *
     * @return
     */
    private void wechatRefund(PurchasePO purchaseInfo, int remainPoint, int remainCash, String userAgent) throws URISyntaxException, IOException {
        //8. 调用微信退款接口进行退款
        OrderResult outTradeInfo = transactionService.getOutTradeNo(purchaseInfo.getId());
        if (!outTradeInfo.getTradeState().equals("SUCCESS")) {
            throw new FailException("订单发生异常，请联系管理员！");
        }
        OrderResult order = transactionService.getOrder(outTradeInfo.getTransactionId());

        if (null == order.getTransactionId()) {
            throw new FailException("微信订单号为空！");
        }

        PaymentPO cashPayment = paymentMapper.findPaymentByPaymentId(purchaseInfo.getPayments()[0]);
        RefundResult refundInfo = transactionService.applyRefunds(order.getTransactionId(), purchaseInfo.getId(), "system", remainCash, cashPayment.getCash());

        //9. 退款如果返回处理中，则返回206状态
        //10. 如果返回退款已关闭，则返回410状态
        //11. 如果返回异常则返回500状态
        if (refundInfo.getStatus().equals("RESOURCE_NOT_EXISTS")) {
            throw new FailException("请检查退款单号是否有误以及订单状态是否正确，如：未支付、已支付未退款");
        } else if (refundInfo.getStatus().equals("SIGN_ERROR")) {
            throw new FailException("MCH_NOT_EXISTS");
        } else if (refundInfo.getStatus().equals("SIGN_ERROR")) {
            throw new FailException("请检查MCHID是否正确");
        } else if (refundInfo.getStatus().equals("PARAM_ERROR")) {
            throw new FailException("请求参数错误，请检查参数再调用退款查询");
        } else if (refundInfo.getStatus().equals("SUCCESS")) {
            //成功退款
            purchaseMapper.updatePurchaseStatusById(purchaseInfo.getId(), PurchaseEnum.Status.Refunded.getLabel());
            paymentMapper.updatePaymentStatusById(purchaseInfo.getPayments()[0], PaymentEnum.Status.Refunded.getLabel());
            paymentMapper.updatePaymentStatusById(purchaseInfo.getProductId(), PaymentEnum.Status.Refunded.getLabel());

            //资金表id
            String paymentId = "pay-" + SnowflakeUtil.nextId();
            //订单表id
            String paymentId1 = "pay-" + SnowflakeUtil.nextId();

            String metaJson = generateMetaInfo(purchaseInfo.getUid(), null, userAgent);
            //资金表信息
            PaymentPO paymentPO = new PaymentPO(paymentId, purchaseInfo.getUid(), PaymentEnum.Type.Redeem.getLabel(), remainCash, -remainPoint, PaymentEnum.Status.Refunded.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseInfo.getId(), null, metaJson, null, null, null, null, null);
            paymentMapper.addPayment(paymentPO);
            PaymentPO paymentPO1 = new PaymentPO(paymentId1, purchaseInfo.getUid(), PaymentEnum.Type.Refund.getLabel(), -remainCash, remainPoint, PaymentEnum.Status.Refunded.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseInfo.getId(), null, metaJson, null, null, null, null, null);
            paymentMapper.addPayment(paymentPO1);


        } else if (refundInfo.getStatus().equals("PROCESSING")) {
            // 正在退款
            purchaseMapper.updatePurchaseStatusById(purchaseInfo.getId(), PurchaseEnum.Status.Refunded.getLabel());
            paymentMapper.updatePaymentStatusById(purchaseInfo.getPayments()[0], PaymentEnum.Status.Refunded.getLabel());
            paymentMapper.updatePaymentStatusById(purchaseInfo.getProductId(), PaymentEnum.Status.Refunded.getLabel());

            //资金表id
            String paymentId = "pay-" + SnowflakeUtil.nextId();
            //订单表id
            String paymentId1 = "pay-" + SnowflakeUtil.nextId();

            String metaJson = generateMetaInfo(purchaseInfo.getUid(), null, userAgent);
            //资金表信息
            PaymentPO paymentPO = new PaymentPO(paymentId, purchaseInfo.getUid(), PaymentEnum.Type.Redeem.getLabel(), remainCash, -remainPoint, PaymentEnum.Status.Refunded.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseInfo.getId(), null, metaJson, null, null, null, null, null);
            paymentMapper.addPayment(paymentPO);
            PaymentPO paymentPO1 = new PaymentPO(paymentId1, purchaseInfo.getUid(), PaymentEnum.Type.Refund.getLabel(), -remainCash, 0, PaymentEnum.Status.Refunded.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseInfo.getId(), null, metaJson, null, null, null, null, null);
            paymentMapper.addPayment(paymentPO1);

        } else {
            throw new FailException("其他未知错误！");
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
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public PurchasePO purchase(String uid, String userAgent, SkuPO skuInfo, Integer amount, String purchaseId, String paymentId, String metaJson) {
        //3.2 如果类型是点数充值，则需要需要生成一条订单信息，两条资金表记录(一条资金进账，另一条资金转点数记录)
        //订单表信息
        PurchasePO
                purchasePO = new PurchasePO(purchaseId, uid, null, Objects.requireNonNull(skuInfo.getId()), amount, 0, paymentId, PurchaseEnum.Status.Finished.getLabel(), null, null, null);
        paymentMapper.addPurchase(purchasePO);
        //资金表信息
        int pointCount = Objects.requireNonNull(skuInfo.getPoints()) * amount;
        PaymentPO paymentPO = new PaymentPO(paymentId, uid, PaymentEnum.Type.Redeem.getLabel(), Objects.requireNonNull(skuInfo.getPrice()), pointCount, PaymentEnum.Status.Succeed.getLabel(),
                PaymentEnum.Channel.WeChat.getLabel(), purchaseId, null, metaJson, null, null, null, null, null);
        paymentMapper.addPayment(paymentPO);
        return paymentMapper.getPurchaseById(purchaseId, uid);
    }


    @Override
    public PurchaseMeta evaluation(String uid, String productId, String skuId, int amount) {
        //订单表id
        String purchaseId = "pur-" + SnowflakeUtil.nextId();
        //1.根据skuId查询
        PurchasePO
                purchasePO = new PurchasePO(purchaseId, uid, null, skuId, amount, 0, productId, PurchaseEnum.Status.Pending.getLabel(), null, null, null);
        paymentMapper.addPurchase(purchasePO);
        int neededPoint = calculateSkuAmountAggregate(skuId, amount);
        return new PurchaseMeta(purchaseId, neededPoint, productId);
    }

    /**
     * 费用计算
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public String judgeCost(String uid, String productId, String purchaseId) {
        PurchasePackDoubleList purchasePackDoubleList = null;
        int[] envelopNeedPoint = new int[1];

        //获取用户最新的点数消费记录
        PaymentPO lastCurrentUserCostRecord = paymentMapper.getLastCurrentUserCostRecord(uid, PaymentEnum.Type.Spending.getLabel());

        //获取订单信息
        PurchasePO purchaseInfo = purchaseMapper.findByPurchaseId(purchaseId);
        if (!productId.equals(purchaseInfo.getProductId())) {
            throw new FailException("传入的任务编号不是订单中的任务编号！");
        }
        //订单表id
        List<String> payments = new ArrayList<>();

        //如果用户没有消费，第一次进行消费
        if (null == lastCurrentUserCostRecord) {
            //那么就去检查所有记录
            purchasePackDoubleList = getUserLastWalletInfo(uid);
            //得到这次执行任务需要的点数
            int neededPoint = calculateSkuAmountAggregate(purchaseInfo.getSkuId(), purchaseInfo.getAmount());
            envelopNeedPoint[0] = neededPoint;

            if (purchasePackDoubleList.length == 0) {
                throw new FailException("未发现有可用的钱包！");
            }
            recordNeededPoint(uid, purchaseId, payments, purchasePackDoubleList.getFirst(), envelopNeedPoint, purchasePackDoubleList);
        } else {
            //如果有消费记录，获取消费记录下，是否存在点数增加的数据
            List<PaymentPO> paymentPOS = paymentMapper.listLastUserRechargeRecord(uid, lastCurrentUserCostRecord.getCreatedAt());
            paymentPOS.add(lastCurrentUserCostRecord);
            //那么就获取能用的最老的钱包
            purchasePackDoubleList = getUserLastWalletInfo(uid, paymentPOS);

            //得到这次执行任务需要的点数
            int neededPoint = calculateSkuAmountAggregate(purchaseInfo.getSkuId(), purchaseInfo.getAmount());
            envelopNeedPoint[0] = neededPoint;

            if (purchasePackDoubleList.length == 0) {
                throw new FailException("未发现有可用的钱包！");
            }
            try {
                recordNeededPoint(uid, purchaseId, payments, purchasePackDoubleList.getFirst(), envelopNeedPoint, purchasePackDoubleList);
            } catch (FailException e) {
                //如果是因为点数不够失败，应该到用户所有子钱包中全量
                purchasePackDoubleList = getUserLastWalletInfo(uid);
                recordNeededPoint(uid, purchaseId, payments, purchasePackDoubleList.getFirst(), envelopNeedPoint, purchasePackDoubleList);
            }
        }
        if (purchasePackDoubleList.getLength() == 0) {
            throw new FailException("当前用户无点数可用！");
        }

        return purchaseId;
    }

    /**
     * 计算任务需要的费(递归)
     *
     * @param uid
     * @param purchasePackInfoVO
     * @param envelopNeedPoint
     * @param purchasePackDoubleList
     */
    private void recordNeededPoint(String uid, String purchaseId, List<String> payments, PurchasePackInfoVO purchasePackInfoVO, int[] envelopNeedPoint, PurchasePackDoubleList purchasePackDoubleList) {
        if (null == purchasePackInfoVO) {
            throw new FailException("已经不够点数来扣费！还需要" + Math.abs(envelopNeedPoint[0]) + "点");
        }

        //得到剩余点数
        int remainPoint = calculateAppointWalletRemain(uid, purchasePackInfoVO.getProductId());
        //如果剩余点数为0，证明当前钱包已经没有钱，快速换下一个
        if (remainPoint == 0) {
            recordNeededPoint(uid, purchaseId, payments, purchasePackDoubleList.getNext(purchasePackInfoVO), envelopNeedPoint, purchasePackDoubleList);
        } else if (remainPoint + envelopNeedPoint[0] < 0) {
            //如果不够扣的，需要下一个点数套餐来扣
            //如果不够，分段扣费
            String newRecodePaymentId = "pay-" + SnowflakeUtil.nextId();
            payments.add(newRecodePaymentId);
            PaymentPO paymentInfo = new PaymentPO(newRecodePaymentId, uid, PaymentEnum.Type.Spending.getLabel(), 0, -remainPoint, PaymentEnum.Status.Succeed.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseId, null, null, null, null, new Date(), purchasePackInfoVO.getProductId(), purchasePackInfoVO.getProductId());
            paymentMapper.addPayment(paymentInfo);

            envelopNeedPoint[0] = (envelopNeedPoint[0] + remainPoint);
            //分段记录
            recordNeededPoint(uid, purchaseId, payments, purchasePackDoubleList.getNext(purchasePackInfoVO), envelopNeedPoint, purchasePackDoubleList);
        } else {
            //如果够扣直接记录扣款
            String newRecodePaymentId = "pay-" + SnowflakeUtil.nextId();
            payments.add(newRecodePaymentId);
            PaymentPO paymentInfo = new PaymentPO(newRecodePaymentId, uid, PaymentEnum.Type.Spending.getLabel(), 0, envelopNeedPoint[0], PaymentEnum.Status.Succeed.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseId, null, null, null, null, new Date(), purchasePackInfoVO.getProductId(), purchasePackInfoVO.getProductId());
            paymentMapper.addPayment(paymentInfo);
            purchaseMapper.updatePurchasePaymentsById(purchaseId, payments.stream().toArray(String[]::new));

            //如果扣完费，订单状态改为完成
            purchaseMapper.updatePurchaseStatusById(purchaseId, PurchaseEnum.Status.Finished.getLabel());
        }
    }

    /**
     * 获取久远的钱包信息(用于用户已经有消费记录的情况)
     *
     * @param uid
     * @param paymentPOList
     * @return
     */
    private PurchasePackDoubleList getUserLastWalletInfo(String uid, @NotNull List<PaymentPO> paymentPOList) {
        PurchasePackDoubleList purchasePackDoubleList = new PurchasePackDoubleList();
        paymentPOList.forEach(item -> {
            if (null != item.getSlot()) {
                PaymentPO payment = paymentMapper.getPaymentById(item.getSlot(), uid);
                PurchasePackInfoVO userPurchasePointPack = purchaseMapper.getUserPurchasePointPack(uid, payment.getPurchaseId());
                if (null == userPurchasePointPack) {
                    //这里需要记录一下为什么为null，需要打印日志
                } else {
                    purchasePackDoubleList.dynamicAdd(userPurchasePointPack);
                }
            }
        });

        if (purchasePackDoubleList.getLength() == 0) {
            return getUserLastWalletInfo(uid);

        }
        return purchasePackDoubleList;
    }

    /**
     * 计算最老的钱包 (双向链表、递归，将点数放到合适的位置)(用于用户初次消费的情况)
     */
    public PurchasePackDoubleList getUserLastWalletInfo(String uid) {
        PurchasePackDoubleList purchasePackDoubleList = new PurchasePackDoubleList();

        //得到赠送的点数记录
        List<PurchasePackInfoVO> purchasePackInfoVOList = purchaseMapper.listUserPurchasePointPack(uid, SkuEnum.Type.PointGift.getLabel());
        //如果赠送的点数记录是空，那么需要对有序的用户钱包集合做初始化
        if (purchasePackInfoVOList != null) {
            purchasePackInfoVOList.forEach(purchasePackDoubleList::dynamicAdd);
        }

        purchasePackInfoVOList = purchaseMapper.listUserPurchasePointPack(uid, SkuEnum.Type.PointPack.getLabel());
        if (purchasePackInfoVOList != null) {
            purchasePackInfoVOList.forEach(purchasePackDoubleList::dynamicAdd);
        }
        return purchasePackDoubleList;
    }

    /**
     * 计算指定钱包的剩余点数
     *
     * @param uid
     * @param paymentId
     */
    private int calculateAppointWalletRemain(String uid, String paymentId) {
        PaymentPO paymentInfo = paymentMapper.getPaymentById(paymentId, uid);
        int remainPoint = paymentMapper.countPaymentByUidAndSlot(uid, paymentId);
        int resultPoint = paymentInfo.getPoint() + remainPoint;
        if (resultPoint < 0) {
            throw new FailException("点数计算出错，计算到的点数出现负数！");
        }
        return resultPoint;
    }


    /**
     * 计算某个sku和数量下，应花费的点数总计
     *
     * @param skuId
     * @param amount
     * @return
     */
    private int calculateSkuAmountAggregate(String skuId, int amount) {
        if (amount <= 0) {
            throw new FailException("要执行的数量不能小于等于0");
        }
        SkuPO skuInfo = pointMapper.getPointPriceBySkuId(skuId);
        if (null == skuInfo) {
            throw new NotFoundException("没有发现id为" + skuId + "的sku信息");
        }
        return skuInfo.getPoints() * amount;
    }


    /**
     * 退点
     *
     * @param uid
     * @param taskId
     * @param amount 要退的点数
     */
    @Override
    public void refundPoint(String uid, String taskId, int amount) {
        //1. 通过taskId得到订单信息
        PurchasePO purchaseInfo = purchaseMapper.findByPurchaseByProductId(taskId);
        if (null == purchaseInfo) {
            throw new FailException("未发现与当前任务有关的订单");
        }
        //2.通过流水记录找到用户的钱包
        List<PaymentPO> paymentPOS = paymentMapper.listPayment(purchaseInfo.getId());
        //3.找到子钱包后，计算钱包哪一个比较新，将返还的点数放回钱包中
        PurchasePackDoubleList userNewestWalletInfo = getUserLastWalletInfo(uid, paymentPOS);
        //4. 由于代码复用，因此得到的钱包最老的在第一个，要找最新的子钱包需要得到last值，倒推
        PurchasePackInfoVO purchasePackInfoVO = userNewestWalletInfo.getLast();
        //5.得到sku执行需要的点数信息
        amount = calculateSkuAmountAggregate(purchaseInfo.getSkuId(), amount);

        int[] envelopPoint = new int[1];
        envelopPoint[0] = Math.abs(amount);
        repoint(uid, purchaseInfo.getId(), envelopPoint, purchasePackInfoVO, userNewestWalletInfo);
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
    public void repoint(String uid, String purchaseId, int[] envelopPoint, PurchasePackInfoVO purchasePackInfoVO, PurchasePackDoubleList userNewestWalletInfo) {
        //5. 得到钱包了，要先计算一下钱包总点数是多少
        PaymentPO payment = paymentMapper.getPaymentById(purchasePackInfoVO.getProductId(), uid);
        //6. 得到用了多少点
        int remainPoint = paymentMapper.countPaymentByUidAndSlot(uid, purchasePackInfoVO.getProductId());

        //如果这个钱包没有消费，证明根本无法再退点进去，直接找下一个钱包
        if (remainPoint == 0) {
            repoint(uid, purchaseId, envelopPoint, userNewestWalletInfo.getPrevious(purchasePackInfoVO), userNewestWalletInfo);
        } else if (Math.abs(remainPoint) < envelopPoint[0]) {
            envelopPoint[0] = envelopPoint[0] + remainPoint;
            //证明一个子钱包退款装不下，需要再装一部分到其他子钱包中
            String newRecodePaymentId = "pay-" + SnowflakeUtil.nextId();
            PaymentPO paymentInfo = new PaymentPO(newRecodePaymentId, uid, PaymentEnum.Type.Refund.getLabel(), 0, Math.abs(remainPoint), PaymentEnum.Status.Succeed.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseId, null, null, null, null, new Date(), payment.getId(), purchasePackInfoVO.getProductId());
            paymentMapper.addPayment(paymentInfo);

            repoint(uid, purchaseId, envelopPoint, userNewestWalletInfo.getPrevious(purchasePackInfoVO), userNewestWalletInfo);
        } else {
            String newRecodePaymentId = "pay-" + SnowflakeUtil.nextId();
            PaymentPO paymentInfo = new PaymentPO(newRecodePaymentId, uid, PaymentEnum.Type.Refund.getLabel(), 0, envelopPoint[0], PaymentEnum.Status.Succeed.getLabel(),
                    PaymentEnum.Channel.WeChat.getLabel(), purchaseId, null, null, null, null, new Date(), payment.getId(), purchasePackInfoVO.getProductId());
            paymentMapper.addPayment(paymentInfo);
        }
    }

}

