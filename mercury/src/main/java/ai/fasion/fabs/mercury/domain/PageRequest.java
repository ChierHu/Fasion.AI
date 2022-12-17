package ai.fasion.fabs.mercury.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Function: 分页限定类 原名pageInfo和com.github.pagehelper.PageInfo重名，因此需要更换
 *
 * @author miluo
 * Date: 2019-06-26 14:29
 * @since JDK 1.8
 */
@ApiModel("分页参数类")
public class PageRequest {

    public PageRequest() {
        this.page = 1;
        this.size = 10;
    }

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }


    @ApiModelProperty(value = "第几页", example = "1")
    private int page;

    @ApiModelProperty(value = "每一页数量", example = "1")
    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
