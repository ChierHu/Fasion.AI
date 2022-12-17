package ai.fasion.fabs.apollo.tasks.vo;

public class SubmitTasksVO {

    private String id;

    private String type;

    private Object status;

    private String link;

    private String purchaseId;

    private Integer point;

    private String taskId;

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SubmitTasksVO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", link='" + link + '\'' +
                ", purchaseId='" + purchaseId + '\'' +
                ", point=" + point +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}
