package ai.fasion.fabs.apollo.assets;

import ai.fasion.fabs.apollo.assets.po.AssetPO;
import ai.fasion.fabs.apollo.assets.vo.AssetPahtVO;
import ai.fasion.fabs.apollo.assets.vo.AssetVO;
import ai.fasion.fabs.apollo.assets.vo.PropertyVO;
import ai.fasion.fabs.vesta.enums.Asset;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssetMapper {
    /**
     * 插入资产信息
     */
    @Insert("INSERT INTO asset (id, owner, owner_id, path, type, status, last_access_at, bundle) VALUES (#{id}, #{owner}, #{ownerId}, #{path}, #{type}, #{status}, #{lastAccessAt}, #{bundle} )")
    void save(AssetPO assetPO);

    /**
     * 根据bundle查询asset中的path路径
     *
     * @param id 资产id
     * @return 资产id和资产路径集合
     */
    @Select(" SELECT id, path FROM asset WHERE bundle = #{id} ")
    List<AssetPahtVO> findPathByBundle(String id);


    @Select(" <script>" +
            " SELECT id AS assetId, type, path  " +
            " FROM asset " +
            " WHERE type = #{type} " +
            " AND status = #{status} " +
            " AND owner_id = #{uid} " +
            " <if test = \"bundleId != null and bundleId != '' \"> " +
            " AND bundle = #{bundleId} " +
            " </if> " +
            " ORDER BY created_at ASC " +
            " </script>")
    @Results({
            @Result(column = "type", property = "type", typeHandler = Asset.Type.TypeHandler.class)
    })
    List<AssetVO> selectInfoByType(Integer type, Integer status, String uid, String bundleId);

    @Update("UPDATE asset SET status = #{status} WHERE id = #{assetId} AND (type = #{type1} OR type = #{type2} OR type = #{type3}  )")
    int updateStatusByAssetId(Integer status, String assetId, Integer type1, Integer type2, Integer type3);

    @Select("SELECT id AS assetId, type, path, owner, owner_id AS ownerId   FROM asset WHERE status IN( #{statusByEnable}, #{statusByDelete}) AND id = #{assetId} ORDER BY created_at ASC ")
    @Results({
            @Result(column = "type", property = "type", typeHandler = Asset.Type.TypeHandler.class)
    })
    PropertyVO findInfoByAssetId(Integer statusByEnable, Integer statusByDelete, String assetId);

    @Select("SELECT path FROM asset WHERE type = #{type}")
    List<AssetPO> findPathByType(Integer type);

}
