package ai.fasion.fabs.apollo.tasks.dto;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/6/1 14:44
 * @since JDK 1.8
 */
public class CreateRequestDTO<T> {

    private String type;
    private T payload;
    private String ownerType;
    private String ownerId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "CreateRequestDTO{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                ", ownerType='" + ownerType + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
