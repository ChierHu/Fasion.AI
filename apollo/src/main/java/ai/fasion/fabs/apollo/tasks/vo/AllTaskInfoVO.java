package ai.fasion.fabs.apollo.tasks.vo;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/29 13:37
 * @since JDK 1.8
 */
public class AllTaskInfoVO {

    private int total;

    private LinkVO links;

    private Object data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public LinkVO getLinks() {
        return links;
    }

    public void setLinks(LinkVO links) {
        this.links = links;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}

