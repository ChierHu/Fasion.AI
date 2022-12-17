package ai.fasion.fabs.diana.domain.dto;

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

    private List<Object> products;

    public Boolean getSpecial() {
        return special;
    }

    public void setSpecial(Boolean special) {
        this.special = special;
    }

    public List<Object> getProducts() {
        return products;
    }

    public void setProducts(List<Object> products) {
        this.products = products;
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
}
