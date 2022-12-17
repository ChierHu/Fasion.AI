package ai.fasion.fabs.diana.service;

import ai.fasion.fabs.diana.domain.po.SkuPO;
import ai.fasion.fabs.diana.domain.vo.SkuInfoVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface SkuService {

    /**
     * 查询sku套餐
     * @return
     */
    List<SkuInfoVO> skuInfoList(String type);

    /**
     * 增加sku套餐
     */
    void addSku(SkuInfoVO skuInfoVO) throws JsonProcessingException;

    /**
     * 修改sku(快照)
     */
    void updateSku(SkuInfoVO skuInfoVO, String sku) throws JsonProcessingException;

    /**
     * 根据id修改status
     */
    void updateStatus(String status, String id);
}
