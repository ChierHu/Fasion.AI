package ai.fasion.fabs.apollo.assets.vo;

import ai.fasion.fabs.vesta.enums.Asset;

public class AssetVO {

    private String assetId;

    private Asset.Type type;

    private String path;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Asset.Type getType() {
        return type;
    }

    public void setType(Asset.Type type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "AssetVO{" +
                "assetId='" + assetId + '\'' +
                ", type=" + type +
                ", path='" + path + '\'' +
                '}';
    }
}
