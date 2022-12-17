package ai.fasion.fabs.apollo.assets.vo;

public class AssetPahtVO {

    private String id;

    private  String path ;

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

    @Override
    public String toString() {
        return "AssetPahtVO{" +
                "id='" + id + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
