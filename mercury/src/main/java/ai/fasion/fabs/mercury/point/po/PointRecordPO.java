package ai.fasion.fabs.mercury.point.po;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 点数记录表
 */
public class PointRecordPO {

    /**
     * 点数记录id(订单号)
     */
    private String id;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 类别(充值、批量换脸、换背景、试效果)
     */
    private String type;

    /**
     * 充值或消费的点数(消费以1点起，不能小于1)
     */
    private Integer amount;

    /**
     * 类别为充值时，与账单表id相关联；如果为消费(批量换脸、换背景、试效果)，与任务表关联
     */
    private String ctxId;

    /**
     * 状态(pending、succeeded、 cancelled)
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 修改时间
     */
    private Date updatedAt;

    /**
     * 完成时间
     */
    private Date finishedAt;

    @Override
    public String toString() {
        return "PointRecordPO{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", ctxId='" + ctxId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", finishedAt=" + finishedAt +
                '}';
    }
}
