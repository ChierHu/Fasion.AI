package ai.fasion.fabs.vesta.utils;

import java.io.UnsupportedEncodingException;

public class IdUtils {
    //订单id
    public static String generatePurchaseId(){
        return String.valueOf(IdGenerator.shortIdGenerator.nextIdRandom());
    }
    //流水id
    public static String generatePayMentId(){
        return String.valueOf(IdGenerator.shortIdGenerator.nextIdRandom());
    }
    //sku id
    public static String generateSkuId() throws UnsupportedEncodingException {
        byte[] bytes = String.valueOf(IdGenerator.shortIdGenerator.nextIdRandom()).getBytes("UTF-8");
        //生成的id base32 编码
        return BaseCodeUtils.encodeBase32(bytes);
    }

    //sku快照 id
    public static String generateSkuSnapshotId() throws UnsupportedEncodingException {
        byte[] bytes = String.valueOf(IdGenerator.shortIdGenerator.nextIdRandom()).getBytes("UTF-8");
        //生成的id base16 编码
        return BaseCodeUtils.base16Encode(bytes);
    }

    //素材 id
    public static String generateMaterialId() throws UnsupportedEncodingException {
        byte[] bytes = String.valueOf(IdGenerator.shortIdGenerator.nextIdRandom()).getBytes("UTF-8");
        //生成的id base62 编码
        return BaseCodeUtils.encodeBase62(bytes).toString();
    }

    //任务 id
    public static String generateTaskId() throws UnsupportedEncodingException {
        byte[] bytes = String.valueOf(IdGenerator.shortIdGenerator.nextIdRandom()).getBytes("UTF-8");
        //生成的id base32 编码
        return BaseCodeUtils.encodeBase32(bytes);
    }

}
