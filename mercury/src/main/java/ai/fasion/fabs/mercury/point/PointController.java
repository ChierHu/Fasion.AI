package ai.fasion.fabs.mercury.point;

import ai.fasion.fabs.mercury.domain.PageRequest;
import ai.fasion.fabs.mercury.payment.vo.AllPointInfoVO;
import ai.fasion.fabs.mercury.point.vo.SkuVO;

import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Integer getPointsBalance(@RequestParam(value = "uid") String uid) {
        int result = pointService.getPointsBalance(uid);
        return result;
    }

    @ApiOperation("获取点数套餐信息")
    @GetMapping("/points/packs")
    public List<SkuVO> getPointsPacks() {
        List<SkuVO> list = pointService.getPointsPacks();
        return list;
    }
}
