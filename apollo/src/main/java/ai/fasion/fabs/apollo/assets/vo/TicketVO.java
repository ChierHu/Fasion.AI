package ai.fasion.fabs.apollo.assets.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Function: 下发凭证
 *
 * @author miluo
 * Date: 2021/5/27 15:58
 * @since JDK 1.8
 */
public class TicketVO {

    /**
     * 凭证唯一编号
     */
    private Long id;

    /**
     * 所属bucket
     */
    private String bucket;

    /**
     * ak
     */
    @JsonProperty("access_key")
    private String accessKey;

    /**
     * 签名
     */
    private String signature;

    /**
     * 对象key
     */
    @JsonProperty("object_key")
    private String objectKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "TicketVO{" +
                "id=" + id +
                ", bucket='" + bucket + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", signature='" + signature + '\'' +
                ", objectKey='" + objectKey + '\'' +
                '}';
    }
}
