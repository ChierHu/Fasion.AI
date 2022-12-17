package ai.fasion.fabs.apollo.assets.pojo;

import java.io.Serializable;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/27 15:44
 * @since JDK 1.8
 */
public class Ticket implements Serializable {

    private static final long serialVersionUID = -5775962683697288494L;
    /**
     * 类型 形象、素材
     */
    private String type;
    /**
     * 金山云路径
     */
    private String path;

    private String uid;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
