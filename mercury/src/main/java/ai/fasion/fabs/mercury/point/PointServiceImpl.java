package ai.fasion.fabs.mercury.point;

import ai.fasion.fabs.mercury.domain.PageRequest;
import ai.fasion.fabs.mercury.payment.vo.AllPointInfoVO;
import ai.fasion.fabs.mercury.payment.vo.Link;
import ai.fasion.fabs.mercury.point.vo.SkuVO;
import ai.fasion.fabs.vesta.enums.SkuEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function:点数服务
 *
 * @author miluo
 * Date: 2021/8/19 17:40
 * @since JDK 1.8
 */
@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointMapper pointMapper;

    @Override
    public int getPointsBalance(String uid) {
        // todo 获取点数余额 并且返回给前端
        int result = pointMapper.getUserPoints(uid);
        return result;
    }

    @Override
    public List<SkuVO> getPointsPacks() {
        List<SkuVO> pointPackList = pointMapper.selectPointsPacks(SkuEnum.Status.Enable.getLabel(), SkuEnum.Type.PointPack.getLabel());
        return pointPackList;
    }
}
