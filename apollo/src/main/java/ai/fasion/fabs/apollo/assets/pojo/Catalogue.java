package ai.fasion.fabs.apollo.assets.pojo;

/**
 * Function: 记录金山云路径和分配id
 *
 * @author miluo
 * Date: 2021/5/27 16:36
 * @since JDK 1.8
 */
public class Catalogue {

    /**
     * id
     */
    private String id;
    /**
     * kec oss path
     */
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
