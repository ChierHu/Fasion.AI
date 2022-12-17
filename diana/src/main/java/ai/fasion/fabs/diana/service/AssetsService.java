package ai.fasion.fabs.diana.service;

import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;

public interface AssetsService {

    AllInfoVO findAll(String type, PageRequest pageRequest, Integer scale, String uid);

    int delectByAssetId(String assetsId);
}
