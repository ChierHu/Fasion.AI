package ai.fasion.fabs.vesta;

import ai.fasion.fabs.vesta.utils.KsOssUtil;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/5/27 19:46
 * @since JDK 1.8
 */
public class TestKsOss {
    public static void main(String[] args) {
        //通过外链访问图片
        //System.out.println(KsOssUtil.getInstance().show("starlight", "dev/configure/defaultAvatar.png"));

        //通过外链上传信息
        System.out.println(KsOssUtil.getInstance().upload("starlight", "dev/configure/abc.mp4"));



    }
}
