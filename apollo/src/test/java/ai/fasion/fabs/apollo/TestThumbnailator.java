package ai.fasion.fabs.apollo;

import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.vesta.utils.KsOssUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Function: 缩略图测试
 *
 * @author miluo
 * Date: 2021/6/23 10:56
 * @since JDK 1.8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestThumbnailator {

    @Test
    public void show() throws IOException {
        Thumbnails.of("/Users/bainingchen/Desktop/ddd.jpg")
                .scale(0.5f)
                .watermark(Positions.CENTER, ImageIO.read(new File("/Users/bainingchen/Desktop/mask.png")), 0.5f)
                .outputQuality(0.8f)
                .toFile("/Users/bainingchen/Desktop/ddd_min.jpg");
    }

    @Test
    public void show1() {
        String s = KsOssUtil.getInstance().shiftName("fasion-devel", "123/16227.png@base@tag=imgScale&p=50");
        System.out.println(s);
    }
}
