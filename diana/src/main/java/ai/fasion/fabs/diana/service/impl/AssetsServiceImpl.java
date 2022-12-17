package ai.fasion.fabs.diana.service.impl;

import ai.fasion.fabs.diana.common.ReturnFormat;
import ai.fasion.fabs.diana.constant.KsOssConstant;
import ai.fasion.fabs.diana.domain.po.AssetPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.domain.vo.AssetVO;
import ai.fasion.fabs.diana.mapper.AssetsMapper;
import ai.fasion.fabs.diana.service.AssetsService;
import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.enums.Status;
import ai.fasion.fabs.vesta.enums.UserTypeEnum;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetsServiceImpl implements AssetsService {

    private final AssetsMapper assetsMapper;
    private final KsOssConstant ksOssConstant;
    private final ReturnFormat returnFormat;

    public AssetsServiceImpl(AssetsMapper assetsMapper, KsOssConstant ksOssConstant, ReturnFormat returnFormat) {
        this.assetsMapper = assetsMapper;
        this.ksOssConstant = ksOssConstant;
        this.returnFormat = returnFormat;
    }


    @Override
    public AllInfoVO findAll(String type, PageRequest pageRequest, Integer scale, String uid) {
        List<AssetPO> list = new ArrayList<>();
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        if (null == type ||  type.isEmpty() ) {
            //Asset.Type.assetTypeOf(type)这个枚举传值不能传null，否则报错
            list = assetsMapper.selectByType(null, Status.Type.Delete.getCode(), UserTypeEnum.USER.getName(), uid);
        }else {
            //当type不为null，传入枚举并获取相对应的数值传入Mapper层查询出与其相关的素材list
            list = assetsMapper.selectByType(Asset.Type.assetTypeOf(type).getCode(), Status.Type.Delete.getCode(), UserTypeEnum.USER.getName(), uid);
        }
        //调用包含total，link的返回格式封装类
        List<AssetVO> assetVOList = new ArrayList<>(list.size());
        list.forEach(v -> {
            //获取图片地址path，并且图片缩略设置为75%
            String path = KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), v.getPath() + "@base@tag=imgScale&p=" + scale);
            //把assets素材数据保存到AssetVO实体类
            AssetVO assetVO = new AssetVO(v.getId(), v.getType().getLabel(), v.getStatus().getLabel(), path, v.getOwnerId());
            //通过list数组把查询到的assets素材数据进行存储
            assetVOList.add(assetVO);
        });
        //所有的返回形式，数据统一保存到AllInfoVO
        AllInfoVO allInfoVO = returnFormat.format(list);
        allInfoVO.setData(assetVOList);
        return allInfoVO;
    }

    @Override
    public int delectByAssetId(String assetsId) {
        //素材删除只进行逻辑删除
        return assetsMapper.delectByAssetId(Status.Type.Delete.getCode(), assetsId);
    }
}
