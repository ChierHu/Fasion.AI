package ai.fasion.fabs.diana.service.impl;

import ai.fasion.fabs.diana.domain.po.SkuPO;
import ai.fasion.fabs.diana.domain.vo.PropsInfo;
import ai.fasion.fabs.diana.domain.vo.SkuInfoVO;
import ai.fasion.fabs.diana.domain.vo.SkuOtherInfoVO;
import ai.fasion.fabs.diana.domain.vo.SkuPropsInfoVO;
import ai.fasion.fabs.diana.mapper.SkuMapper;
import ai.fasion.fabs.diana.service.SkuService;
import ai.fasion.fabs.vesta.Utils;
import ai.fasion.fabs.vesta.enums.SkuEnum;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.utils.ClassCompareUtil;
import ai.fasion.fabs.vesta.utils.SnowflakeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SkuServiceImpl implements SkuService {

    private final SkuMapper skuMapper;

    private final ObjectMapper objectMapper;

    public SkuServiceImpl(SkuMapper skuMapper, ObjectMapper objectMapper) {
        this.skuMapper = skuMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SkuInfoVO> skuInfoList(String type) {
        //查询套餐数据
        List<SkuPO> skuPOList = skuMapper.skuInfoList(type);
        List<SkuInfoVO> skuInfoList = new ArrayList<>();
        skuPOList.forEach(v->{
            SkuInfoVO skuInfoVO = new SkuInfoVO();
            BeanUtils.copyProperties(v, skuInfoVO);
            try {
                //josn属性转换成Props实体类中的值
                PropsInfo propsInfo = objectMapper.readValue(v.getProps(), PropsInfo.class);
                //把Props实体类数据保存skuInfoVO中的Props属性
                skuInfoVO.setProps(propsInfo);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            //skuInfoList存储skuInfoVO对象
            skuInfoList.add(skuInfoVO);
        });
        return skuInfoList;
}

    @Override
    public void addSku(SkuInfoVO skuInfoVO) throws JsonProcessingException {
        //SkuPropsInfoVO skuPropsInfoVO = new SkuPropsInfoVO(skuInfoVO.getPoints(), skuInfoVO.getExpirationPeriod());
        String props = objectMapper.writeValueAsString(skuInfoVO.getProps());
        SkuPO skuPO = new SkuPO(Utils.hashId(SnowflakeUtil.nextId()), Utils.hashId(SnowflakeUtil.nextId()), 1, skuInfoVO.getName(), skuInfoVO.getPrice(),
                SkuEnum.Type.PointPack.getLabel(), skuInfoVO.getSlogan(), SkuEnum.Status.Disable.getLabel(), props, null);
        int result = skuMapper.insertSku(skuPO);
        if (result == 0) {
            throw new FailException("更新失败");
        }
    }

    @Override
    public void updateSku(SkuInfoVO skuInfoVO, String sku) throws JsonProcessingException {
        skuInfoVO.setSku(sku);
        //通过id查询之前的sku数据
        SkuOtherInfoVO skuOtherInfoOther = skuMapper.skuOtherInfoOther(skuInfoVO.getId());
        //json转Props实体类
        PropsInfo propsInfo = objectMapper.readValue(skuOtherInfoOther.getProps(), PropsInfo.class);
        //copy实体类SkuOtherInfoVO到SkuInfoVO中去
        SkuInfoVO skuOldInfo = new SkuInfoVO();
        BeanUtils.copyProperties(skuOtherInfoOther, skuOldInfo);
        //赋值props属性
        skuOldInfo.setProps(propsInfo);
        //比较skuInfoVO和skuInfoVO1
        Map<String, Map<String, Object>> result = ClassCompareUtil.compareFields(skuOldInfo, skuInfoVO);
        //如果结果为0，证明传过来的数据和库里的一样，直接返回成功
        if (result.size() == 0) {
            return;
        }
        //如果进行了改动，查询是否改动字段（status）
        int num;
        //判断Map中是否存在status字段
        if (result.size() == 1 && result.containsKey("status")) {
            //只在原来的数据进行status修改
            num = skuMapper.updateStatus(skuInfoVO.getStatus(), skuInfoVO.getId());
        } else {
            //通过id查询之前的sku数据
            SkuPO findSkuPO = skuMapper.findSkuByid(skuInfoVO.getId());
            //用Props实体类转换成Json形式
            String props = objectMapper.writeValueAsString(skuInfoVO.getProps());
            //每次更新一次版本加1
            int revision = findSkuPO.getRevision() + 1;
            SkuPO skuPO = new SkuPO(Utils.hashId(SnowflakeUtil.nextId()), sku, revision, skuInfoVO.getName(), skuInfoVO.getPrice(),
                    findSkuPO.getType(), skuInfoVO.getSlogan(), skuInfoVO.getStatus(), props, null);
            num = skuMapper.insertSku(skuPO);
        }
        if (num == 0) {
            throw new FailException("更新失败");
        }

    }

    @Override
    public void updateStatus(String status, String id) {
        int result = skuMapper.updateStatus(status, id);
        if (result == 0) {
            throw new FailException("更新套餐状态失败");
        }
    }
}
