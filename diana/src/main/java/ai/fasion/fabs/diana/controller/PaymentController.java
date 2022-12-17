package ai.fasion.fabs.diana.controller;

import ai.fasion.fabs.diana.common.PageRequestInfo;
import ai.fasion.fabs.diana.domain.po.PaymentPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "后台资金功能接口")
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private final PageRequestInfo pageRequestInfo;

    public PaymentController(PaymentService paymentService, PageRequestInfo pageRequestInfo) {
        this.paymentService = paymentService;
        this.pageRequestInfo = pageRequestInfo;
    }


    @ApiOperation("查询点数流水")
    @GetMapping
    public ResponseEntity<Object> paymentList(@RequestParam(value = "uid", required = false) String uid, @RequestParam(value = "type", required = false) String type,
                                              @RequestParam(value = "page", required = false) Integer page){
        //调用分页方法，当page为null，需要默认设置值
        PageRequest pageRequest = pageRequestInfo.pageRequest(page);
        AllInfoVO allPaymentInfo = paymentService.paymentList(uid, type, pageRequest);
        return new ResponseEntity<>(allPaymentInfo, HttpStatus.OK);
    }

    @ApiOperation("通过paymentId查资金流水")
    @GetMapping("/{paymentId}")
    public ResponseEntity<Object> findById(@PathVariable("paymentId") @NotNull @NotEmpty String paymentId){
        PaymentPO paymentPO = paymentService.findById(paymentId);
        return new ResponseEntity<>(paymentPO, HttpStatus.OK);

    }

}
