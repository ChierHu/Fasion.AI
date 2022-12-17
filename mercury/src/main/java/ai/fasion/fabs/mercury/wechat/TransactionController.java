package ai.fasion.fabs.mercury.wechat;

import ai.fasion.fabs.mercury.wechat.pojo.Notify;
import ai.fasion.fabs.vesta.domain.ResponseEmptyEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Function: 微信支付控制类
 *
 * @author miluo
 * Date: 2021/8/27 15:55
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/wechat")
public class TransactionController {

    @GetMapping("/payment/callback")
    public ResponseEntity<Object> sendMessage(@Validated @RequestBody Notify userCodeVO) {
        System.out.println(userCodeVO.toString());
        return new ResponseEntity<>(userCodeVO, HttpStatus.OK);
    }
}
