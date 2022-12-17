package ai.fasion.fabs.apollo.tasks.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/6/1 15:59
 * @since JDK 1.8
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayloadDTO {

    private String source;

    private String target;

    private Boolean special;

    private List<Object> product;

    public List<Object> getProduct() {
        return product;
    }

    public void setProduct(List<Object> product) {
        this.product = product;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Boolean getSpecial() {
        return special;
    }

    public void setSpecial(Boolean special) {
        this.special = special;
    }
}
