package ai.fasion.fabs.apollo.tasks.vo;

/**
 * Function: tasks payload vo
 *
 * @author miluo
 * Date: 2021/5/28 16:02
 * @since JDK 1.8
 */
public class TasksPayloadVO {
    private Object payload;

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "TasksPayloadVO{" +
                "payload=" + payload +
                '}';
    }
}
