package ai.fasion.fabs.apollo.assets.vo;

import ai.fasion.fabs.vesta.enums.Asset;

public class PropertyVO {
    private String assetId;

    private Asset.Type type;

    private String path;

    private String owner;

    private String ownerId;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "PropertyVO{" +
                "assetId='" + assetId + '\'' +
                ", type=" + type +
                ", path='" + path + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
