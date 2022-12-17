package ai.fasion.fabs.apollo.point;

import ai.fasion.fabs.mercury.point.vo.SkuVO;
import ai.fasion.fabs.vesta.enums.SkuEnum;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.utils.RestTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Function:点数服务
 *
 * @author miluo
 * Date: 2021/8/19 17:40
 * @since JDK 1.8
 */
@Service
public class PointServiceImpl implements PointService {

    @Value("${apollo.mercury.url}")
    private String mercuryUrl;

    @Override
    public int getPointsBalance(String uid) {
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = mercuryUrl + "/points/balance?uid="+uid;
        ResponseEntity<Integer> responseEntity = RestTemplateUtils.get(url, Integer.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        Integer result = responseEntity.getBody();
        return result;
    }

    @Override
    public List<SkuVO> getPointsPacks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = mercuryUrl + "/points/packs";
        ResponseEntity<List> responseEntity = RestTemplateUtils.get(url, List.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }
        List<SkuVO> result = responseEntity.getBody();
        return result;
    }
}
