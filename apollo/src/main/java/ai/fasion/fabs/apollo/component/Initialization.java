package ai.fasion.fabs.apollo.component;

import ai.fasion.fabs.apollo.auth.AuthService;
import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.apollo.assets.po.AssetPO;
import ai.fasion.fabs.apollo.assets.AssetMapper;
import ai.fasion.fabs.apollo.temp.TempUserInfoMapper;
import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import com.ksyun.ks3.dto.CannedAccessControlList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Function: 初始化操作
 *
 * @author miluo
 * Date: 2021/6/9 17:34
 * @since JDK 1.8
 */
@Component
public class Initialization {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${apollo.versionName}")
    private String versionName;

    private final KsOssConstant ksOssConstant;
    private final AssetMapper assetMapper;
    private final TempUserInfoMapper tempUserInfoMapper;
    private final AuthService authService;

    public Initialization(KsOssConstant ksOssConstant, AssetMapper assetMapper, TempUserInfoMapper tempUserInfoMapper, AuthService authService) {
        this.ksOssConstant = ksOssConstant;
        this.assetMapper = assetMapper;
        this.tempUserInfoMapper = tempUserInfoMapper;
        this.authService = authService;
    }


    /**
     * 扫描金山云指定位置是否存在指定名称的图，存在就不插入，不存在就更新
     */
    public void scanImages(String inputFilePath, Asset.Type type) {
        String imageUrl = "data/system/assets/" + type.getLabel().replace("system-", "").replace("-", "/") + "/";

        //判断数据库是否存在背景图片
        List<AssetPO> pathUrl = assetMapper.findPathByType(type.getCode());

        //只输出文件
        try (Stream<Path> paths = Files.walk(Paths.get(inputFilePath), 10, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(Files::isRegularFile).map(Path::toString).filter(f -> f.endsWith(".jpg"))
                    .forEach(item -> {
                        File file = new File(item);
                        // 获取文件文件名
                        String originalFilename = file.getName();
                        boolean result = pathUrl.stream().anyMatch(v -> (v.getPath().substring(v.getPath().lastIndexOf("/") + 1).trim()).equals(originalFilename.trim()));
                        if (!result) {
                            // 上传金山云对象存储
                            KsOssUtil.getInstance().putObject(ksOssConstant.getBucketName(), imageUrl + originalFilename, file);
                            // 保存到数据库中
                            assetMapper.save(new AssetPO(imageUrl + originalFilename, type.getCode()));
                        }
                    });
        } catch (IOException ignored) {
        }
    }


    /**
     * 不属于资产的，项目初始化时需要上传到金山云的数据
     */
    public void scanDefaultImages(String inputFilePath) {
        //只输出文件
        try (Stream<Path> paths = Files.walk(Paths.get(inputFilePath), 2, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(Files::isRegularFile).map(Path::toString)
                    .forEach(item -> {
                        String resultPath = item.replaceAll(inputFilePath + "/", "");
                        //boolean checkExists = KsOssUtil.getInstance().objectExists(ksOssConstant.getBucketName(), resultPath);
                        KsOssUtil.getInstance().putObjectSimple(ksOssConstant.getBucketName(), resultPath, new File(item), CannedAccessControlList.PublicRead);
                    });
        } catch (IOException ignored) {
        }
    }


    public String getAK() {
        return ksOssConstant.getAccessKey();
    }

    public String getSK() {
        return ksOssConstant.getSecretKey();
    }

    /**
     * 将数据库中所有的uid按照规则进行迁移
     */
    public void modifyUserId() {
        List<String> oldUserIds = tempUserInfoMapper.listUserId();
        log.info("get old user id size {}", oldUserIds.size());
        for (String oldUserId : oldUserIds) {
            Integer newUserId = authService.createNewUserId();
            log.info("current old user id is [{}] ,it new user id is [{}]", oldUserId, newUserId);
            tempUserInfoMapper.updateUserId(oldUserId, newUserId.toString());
        }
    }


    public String getCurrentApplicationVersionName() {
        return versionName;
    }
}
