package ai.fasion.fabs.apollo.point;

import ai.fasion.fabs.mercury.point.vo.SkuVO;

import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Function:点数controller
 *
 * @author miluo
 * Date: 2021/8/19 17:44
 * @since JDK 1.8
 */
@RestController
public class PointController {
    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }


    @ApiOperation("获取点数余额")
    @GetMapping("/points/balance")
    public ResponseEntity<Object> getPointsBalance() {
        //获取用户id
        String uid = AppThreadLocalHolder.getUserId();
        int result = pointService.getPointsBalance(uid);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("获取点数套餐信息")
    @GetMapping("/points/packs")
    public ResponseEntity<Object> getPointsPacks() {
        List<SkuVO> list = pointService.getPointsPacks();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
