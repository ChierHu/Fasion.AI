package ai.fasion.fabs.apollo;

import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.apollo.assets.po.AssetPO;
import ai.fasion.fabs.apollo.assets.AssetMapper;
import ai.fasion.fabs.vesta.enums.Asset;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/6/9 17:42
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFilePath {

    @Autowired
    private KsOssConstant ksOssConstant;

    @Autowired
    private AssetMapper assetMapper;

    @Test
    public void show() {
        String dirName = "/Users/bainingchen/Desktop/temp";
        dirName = "/Users/bainingchen/Project/Java/fasion/apollo/build/resources/main/fasion";

//        //过滤出目录
//        try (Stream<Path> paths = Files.walk(Paths.get(dirName))) {
//            paths.filter(Files::isDirectory)
//                    .forEach(System.out::println);
//        } catch (IOException e) {
//
//        }

        //只输出文件
        try (Stream<Path> paths = Files.walk(Paths.get(dirName), 10, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(Files::isRegularFile).map(Path::toString)
                    .forEach(System.out::println);
        } catch (IOException e) {

        }


//        //按后缀名过滤
//        try (Stream<Path> paths = Files.walk(Paths.get(dirName), 2,FileVisitOption.FOLLOW_LINKS)) {
//            paths.map(Path::toString).filter(f -> f.endsWith(".jpeg"))
//                    .forEach(item -> {
//                        File file = new File(item);
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void show1() {
        //手动上传背景图素材
        //Initialization initialization = new Initialization();
        scanImages1("/Users/yangzhou/Desktop/demo", Asset.Type.SystemMattingScene);

    }

    @Test
    public void show2() {
        //手动上传头像素材
        //Initialization initialization = new Initialization();
//        scanImages1("/Users/yangzhou/Desktop/demo1", Asset.Type.SystemFaceSource);


        String text = "402 : [402 : [未发现有可用的钱包！]]";
        System.out.println(text.substring(text.lastIndexOf("[")).replace("]","").replace("[",""));

    }

    @Test
    public void rootPath() {
//        System.out.println(Thread.currentThread().getContextClassLoader().getResource("fasion").getPath());

        String a = Thread.currentThread().getContextClassLoader().getResource("fasion").getPath();
        String b = "/Users/bainingchen/Project/Java/fasion/apollo/build/resources/main/fasion/avatar.png";
        System.out.println("-----------------------------------------");
        System.out.println(a);
        System.out.println(b);
        System.out.println(b.replaceAll(a, ""));

        System.out.println("-----------------------------------------");
    }


    public void scanImages1(String inputFilePath, Asset.Type type) {
        String imageUrl = "data/system/assets/" + type.getLabel().replace("system-", "").replace("-", "/") + "/";

        //只输出文件
        try (Stream<Path> paths = Files.walk(Paths.get(inputFilePath), 10, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(Files::isRegularFile).map(Path::toString).filter(f -> f.endsWith(".jpg"))
                    .forEach(item -> {
                        File file = new File(item);
                        // 获取文件文件名
                        String originalFilename = file.getName();
                        //判断数据库是否存在背景图片
                        List<AssetPO> pathUrl = assetMapper.findPathByType(type.getCode());
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
}
