package ai.fasion.fabs.diana.common;

import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class PageRequestInfo {

    //设置分页page值
    public PageRequest pageRequest(Integer page){
        PageRequest pageRequest;
        if (null == page) {
            pageRequest = new PageRequest();
        } else {
            pageRequest = new PageRequest(page, 10);
        }
        return pageRequest;
    }
}
