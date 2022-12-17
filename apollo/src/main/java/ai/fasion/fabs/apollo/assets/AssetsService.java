package ai.fasion.fabs.apollo.assets;

import ai.fasion.fabs.apollo.assets.pojo.Catalogue;
import ai.fasion.fabs.apollo.assets.pojo.Policy;
import ai.fasion.fabs.apollo.assets.pojo.Ticket;
import ai.fasion.fabs.apollo.assets.vo.AssetVO;
import ai.fasion.fabs.apollo.assets.vo.TicketVO;
import ai.fasion.fabs.vesta.enums.Asset;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Function: asset service
 *
 * @author miluo
 * Date: 2021/5/26 18:08
 * @since JDK 1.8
 */
public interface AssetsService {


    /**
     * 生成凭证信息
     *
     * @param uid       用户id
     * @param assetEnum 上传的类型，素材、形象
     * @param policy    金山云oss policy信息
     * @return
     */
    TicketVO generateTicketInfo(String uid, Asset.Type assetEnum, Policy policy);


    /**
     * 判断ticket是否存在
     *
     * @param ticket
     * @return
     */
    Ticket judgeTicket(String ticket);


    void chancelTicket(String ticket);

    /**
     * 给每个文件分配id
     *
     * @param ticket
     * @return
     */
    List<Catalogue> distribution(Ticket ticket, String ticketId);

    /**
     * 将path下的所有文件名进行修改
     *
     * @param path
     */
    void corrections(String path);

    /**
     * 通过type类型查询asset数据
     *
     * @return
     */
    List<AssetVO> selectByType(String type, String uid, String bundleId);

    /**
     * 通过assetId类型查询asset数据
     *
     * @param assetId
     * @param uid
     * @return
     */
    AssetVO findByAssetId(String assetId, String uid);

    /**
     * 通过assetId逻辑删除素材
     *
     * @param assetId
     * @return
     */
    int deleteByAssetId(String assetId);


    Map<String, Object> uploadByAssetId(String ticketId, MultipartFile file, Ticket ticketInfo) throws IOException;
}
