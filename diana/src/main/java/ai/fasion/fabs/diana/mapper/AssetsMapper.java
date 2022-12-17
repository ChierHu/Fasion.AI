package ai.fasion.fabs.diana.mapper;

import ai.fasion.fabs.diana.domain.po.AssetPO;
import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.enums.Status;
import ai.fasion.fabs.vesta.enums.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssetsMapper {

    @Select(" <script>" +
            " SELECT id, type, status, path, owner_id FROM asset " +
            " WHERE 1=1" +
            " AND status != #{status}" +
            " AND owner = #{owner}" +
            " <if test = \"uid != null and uid != '' \"> " +
            " AND owner_id = #{uid}" +
            " </if>" +
            " <if test = \"type != null \"> " +
            " AND type = #{type}" +
            " </if>" +
            " ORDER BY created_at ASC" +
            " </script>")
    @Results({
            @Result(column = "status", property = "status", typeHandler = Status.Type.TypeHandler.class),
            @Result(column = "type", property = "type", typeHandler = Asset.Type.TypeHandler.class)
    })
    List<AssetPO> selectByType(Integer type, Integer status, String owner, String uid);


    @Update("UPDATE asset SET status = #{status} WHERE id = #{assetsId}")
    int delectByAssetId(Integer status, String assetsId);





}
