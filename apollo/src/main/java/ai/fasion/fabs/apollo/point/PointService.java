package ai.fasion.fabs.apollo.point;

import ai.fasion.fabs.mercury.point.vo.SkuVO;

import java.util.List;

/**
 * Function:点数服务
 *
 * @author miluo
 * Date: 2021/8/19 17:40
 * @since JDK 1.8
 */
public interface PointService {
    int getPointsBalance(String uid);

    List<SkuVO> getPointsPacks();
}
