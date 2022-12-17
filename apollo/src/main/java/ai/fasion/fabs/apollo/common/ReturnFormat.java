package ai.fasion.fabs.apollo.common;

import ai.fasion.fabs.apollo.payment.vo.AllInfoVO;
import ai.fasion.fabs.apollo.tasks.vo.LinkVO;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 把格式返回相同的代码 封装起来
 */
@Component
public class ReturnFormat<T> {

    public AllInfoVO format(List<T> list){
        PageInfo<T> pageInfo = new PageInfo<>(list);
        int total = (int) pageInfo.getTotal();
        LinkVO linkVO = new LinkVO();
        if (pageInfo.getPageNum() < pageInfo.getPages()) {
            linkVO.setNext("/tasks?page=" + (pageInfo.getPageNum() + 1));
        }
        linkVO.setLast("/tasks?page=" + pageInfo.getPages());
        AllInfoVO allInfoVO = new AllInfoVO();
        allInfoVO.setData(pageInfo.getList());
        allInfoVO.setTotal(total);
        allInfoVO.setLinks(linkVO);
        return allInfoVO;
    }
}
