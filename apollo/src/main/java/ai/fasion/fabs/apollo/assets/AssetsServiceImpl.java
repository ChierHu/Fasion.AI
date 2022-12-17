package ai.fasion.fabs.apollo.assets;

import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.apollo.constant.RedisKeyPrefixConstant;
import ai.fasion.fabs.apollo.assets.po.AssetPO;
import ai.fasion.fabs.apollo.assets.pojo.Catalogue;
import ai.fasion.fabs.apollo.assets.pojo.Policy;
import ai.fasion.fabs.apollo.assets.pojo.Ticket;
import ai.fasion.fabs.apollo.assets.vo.AssetVO;
import ai.fasion.fabs.apollo.assets.vo.PropertyVO;
import ai.fasion.fabs.apollo.assets.vo.TicketVO;
import ai.fasion.fabs.vesta.Utils;
import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.enums.Status;
import ai.fasion.fabs.vesta.expansion.AuthorizationException;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.expansion.NotFoundException;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import ai.fasion.fabs.vesta.utils.MultipartFileToFile;
import ai.fasion.fabs.vesta.utils.RestTemplateUtils;
import ai.fasion.fabs.vesta.utils.SnowflakeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.ks3.dto.ObjectListing;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.utils.AuthUtils;
import com.ksyun.ks3.utils.DateUtils;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/26 19:53
 * @since JDK 1.8
 */
@Service
public class AssetsServiceImpl implements AssetsService {
    private static final Logger log = LoggerFactory.getLogger(AssetsServiceImpl.class);

    private static final String USER = "user";

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    private final KsOssConstant ksOssConstant;

    private final RedisKeyPrefixConstant redisKeyPrefixConstant;

    private final AssetMapper assetMapper;

    public AssetsServiceImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, KsOssConstant ksOssConstant, RedisKeyPrefixConstant redisKeyPrefixConstant, AssetMapper assetMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ksOssConstant = ksOssConstant;
        this.redisKeyPrefixConstant = redisKeyPrefixConstant;
        this.assetMapper = assetMapper;
    }

    @Value("${apollo.task.url}")
    private String domainUrl;

    @Value("${apollo.ks3.bucket-name}")
    private String bucketName;


    @Override
    public TicketVO generateTicketInfo(String uid, Asset.Type assetEnum, Policy policy) {
        TicketVO ticket = new TicketVO();

        //生成ticketId
        long ticketId = SnowflakeUtil.nextId();
        ticket.setId(ticketId);
        ticket.setBucket(getBucketName());
        ticket.setAccessKey(getAccessKey());
        policy.setExpiration(DateUtils.convertDate2Str(new DateTime().plusYears(1).toDate(), DateUtils.DATETIME_PROTOCOL.ISO8861));
        policy.setConditions(Arrays.asList(Arrays.asList("eq", "$acl", "private"), Arrays.asList("eq", "$bucket", ksOssConstant.getBucketName()), Arrays.asList("starts-with", "$key", "")));
        ticket.setSignature(getSignature(policy));
        ticket.setObjectKey(getObjectPath(uid, ticketId, assetEnum.getLabel()));
        return ticket;
    }

    /**
     * 判断当前ticket是否存在
     *
     * @param ticket
     * @return
     */
    @Override
    public Ticket judgeTicket(String ticket) {
        return (Ticket) redisTemplate.opsForValue().get(getRedisKey(ticket));
    }

    /**
     * 取消票据
     *
     * @param ticket
     */
    @Override
    public void chancelTicket(String ticket) {
        redisTemplate.delete(getRedisKey(ticket));
    }

    /**
     * 为ticket下所有资源分配id
     *
     * @param ticket
     * @return
     */
    @Override
    public List<Catalogue> distribution(Ticket ticket, String ticketId) {
        List<Catalogue> catalogueList = new ArrayList<>();
        List<ObjectListing> objectListing = KsOssUtil.getInstance().listObjectsWithPrefix(ksOssConstant.getBucketName(), ticket.getPath());
        objectListing.forEach(v -> v.getObjectSummaries().forEach(o -> {
            AssetPO assetPO = new AssetPO();
            Catalogue catalogue = new Catalogue();
            catalogue.setId(Utils.hashId(SnowflakeUtil.nextId()));
            catalogue.setPath(o.getKey());
            catalogueList.add(catalogue);
            //保存到数据库中
            assetPO.setId(Utils.hashId(SnowflakeUtil.nextId()));

            assetPO.setId(String.valueOf(catalogue.getId()));
            assetPO.setOwner("user");
            assetPO.setOwnerId(AppThreadLocalHolder.getUserId());
            assetPO.setPath(catalogue.getPath());
            assetPO.setType(Asset.Type.assetTypeOf(ticket.getType()).getCode());
            assetPO.setStatus(Status.Type.Enable.getCode());
            assetPO.setLastAccessAt(new Date());
            assetPO.setBundle(ticketId);
            assetMapper.save(assetPO);
        }));
        return catalogueList;
    }

    @Override
    public void corrections(String path) {
        //查询出path路径下所有文件路径
        //通过金山云查询path目录下的所有文件
        List<ObjectListing> objectListing = KsOssUtil.getInstance().listObjectsWithPrefix(ksOssConstant.getBucketName(), path);
        objectListing.forEach(v -> {
            if (v.getObjectSummaries().isEmpty()) {
                log.error("path is empty");
                //拼接出url
                throw new FailException("地址不正确");
            }
            v.getObjectSummaries().forEach(o -> {
                //截取文件的后缀
                int num = (o.getKey()).lastIndexOf(".");
                String suffix = (o.getKey()).substring(num);
                //截取原文件前面路径
                String newPath = (o.getKey()).substring(0, (o.getKey()).lastIndexOf("/"));
                //对文件进行复制 （雪花算法重命名）
                KsOssUtil.getInstance().copyObject(ksOssConstant.getBucketName(), newPath + "/" + SnowflakeUtil.nextId() + suffix, ksOssConstant.getBucketName(), o.getKey());
                //删除原文件
                KsOssUtil.getInstance().deleteObject(ksOssConstant.getBucketName(), o.getKey());
            });
        });
    }


    /**
     * get signature
     *
     * @param policy
     * @return
     */
    private String getSignature(Policy policy) {
        String signature = null;
        try {
//            Map<String, Object> policyMap = new HashMap<String, Object>();
//            policyMap.put("expiration", policy.getExpiration());
//            policyMap.put("conditions", policy.getConditions());
//            String policyJson2 = StringUtils.object2json(policyMap);
//            System.out.println(policyJson2);
            String policyJson = objectMapper.writeValueAsString(policy);
            System.out.println(policyJson);
            String policyBase64 = new String(Base64.encodeBase64(policyJson.getBytes()), StandardCharsets.UTF_8);
            signature = AuthUtils.calcSignature(ksOssConstant.getSecretKey(), policyBase64);
        } catch (SignatureException | JsonProcessingException e) {
            throw new Ks3ClientException("计算签名出错", e);
        }
        return signature;
    }


    /**
     * get ak
     *
     * @return
     */
    private String getAccessKey() {
        return ksOssConstant.getAccessKey();
    }


    /**
     * 生成 object key
     *
     * @param uid
     * @param ticketId
     * @param type
     * @return
     */
    private String getObjectPath(String uid, long ticketId, String type) {
        // tring path=/{data, app}/c0/$owner_id/assets/$type/$date/$bundle

        //拼接金山云oss路径
        String path = "data/c0/" + uid + "/assets/" + type.replace("-", "/") + "/" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + ticketId + "/";

        Ticket ticket = new Ticket();
        ticket.setType(type);
        ticket.setPath(path);
        ticket.setUid(uid);
        //将数据存放到radis一天
        redisTemplate.opsForValue().set(getRedisKey(ticketId + ""), ticket, 1, TimeUnit.DAYS);
        return path;
    }

    private String getBucketName() {
        return ksOssConstant.getBucketName();
    }


    private String getRedisKey(String key) {
        return redisKeyPrefixConstant.ticketCode + ":" + key;
    }

    @Override
    public List<AssetVO> selectByType(String type, String uid, String bundleId) {
        Asset.Type types = Asset.Type.assetTypeOf(type);
        List<AssetVO> assetVOList = null;
        if (types.equals(Asset.Type.SystemMattingScene) || types.equals(Asset.Type.SystemFaceSource) || types.equals(Asset.Type.SystemMattingImage)) {
            log.info("system source,bundleId->{}", bundleId);
            assetVOList = assetMapper.selectInfoByType(types.getCode(), Status.Type.Enable.getCode(), "0", bundleId);
        } else {
            log.info("user source，uid->{}，bundleId->{}", uid, bundleId);
            assetVOList = assetMapper.selectInfoByType(types.getCode(), Status.Type.Enable.getCode(), uid, bundleId);
        }
        return assetVOList;
    }

    @Override
    public AssetVO findByAssetId(String assetId, String uid) {
        PropertyVO propertyVO = assetMapper.findInfoByAssetId(Status.Type.Enable.getCode(), Status.Type.Delete.getCode(), assetId);
        if (null == propertyVO) {
            throw new NotFoundException("暂无数据");
        }
        if (USER.equals(propertyVO.getOwner()) && !uid.equals(propertyVO.getOwnerId())) {
            throw new AuthorizationException("当前用户对此数据无权限");
        }
        AssetVO assetVO = new AssetVO();
        assetVO.setAssetId(propertyVO.getAssetId());
        assetVO.setType(propertyVO.getType());
        assetVO.setPath(propertyVO.getPath());
        return assetVO;
    }

    @Override
    public int deleteByAssetId(String assetId) {
        return assetMapper.updateStatusByAssetId(Status.Type.Delete.getCode(), assetId, Asset.Type.FaceSource.getCode(), Asset.Type.FaceTarget.getCode(), Asset.Type.MattingScene.getCode());
    }

    @Override
    public Map<String, Object> uploadByAssetId(String ticketId, MultipartFile file, Ticket ticketInfo) throws IOException {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpHeaders fileHeader = new HttpHeaders();
        fileHeader.setContentType(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())));
        fileHeader.setContentDispositionFormData("file", file.getOriginalFilename());

        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(new ByteArrayResource(file.getBytes()), fileHeader);
        multiValueMap.add("file", fileEntity);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multiValueMap, header);

        String url = domainUrl + "buckets/" + bucketName + "/" + ticketInfo.getPath();

        ResponseEntity<Map> postForEntity = RestTemplateUtils.post(url, httpEntity, Map.class);
        File tempFile = MultipartFileToFile.multipartFileToFile(file);
        if (postForEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            //删除临时文件
            boolean delete = tempFile.delete();
            throw new FailException("系统错误");
        }
        Map result = postForEntity.getBody();
        //获取上传后的路径 从data/xxx/xx...
        assert result != null;
        String paths = result.get("key").toString();
        //把上传记录保存到assets
        AssetPO asset = new AssetPO();
        //保存到数据库中
        asset.setId(Utils.hashId(SnowflakeUtil.nextId()));
        asset.setOwner("user");
        asset.setOwnerId(AppThreadLocalHolder.getUserId());
        asset.setPath(paths);
        asset.setType(Asset.Type.assetTypeOf(ticketInfo.getType()).getCode());
        asset.setStatus(Status.Type.Enable.getCode());
        asset.setLastAccessAt(new Date());
        asset.setBundle(ticketId);
        assetMapper.save(asset);

        Map<String, Object> map = new HashMap<>(3);
        map.put("id", asset.getId());
        map.put("type", ticketInfo.getType());
        map.put("thumbnail", KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), paths));
        //删除临时文件
        boolean delete = tempFile.delete();
        return map;
    }
}
