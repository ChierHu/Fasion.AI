package ai.fasion.fabs.diana.domain.vo;

public class AllInfoVO<T> {
    private int total;

    private LinkVO links;


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

    private Object data;

    @Override
    public String toString() {
        return "AllInfoVO{" +
                "total=" + total +
                ", links=" + links +
                ", data=" + data +
                '}';
    }
}
