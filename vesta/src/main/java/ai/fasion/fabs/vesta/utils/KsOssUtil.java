package ai.fasion.fabs.vesta.utils;

import com.ksyun.ks3.dto.*;
import com.ksyun.ks3.exception.serviceside.NotFoundException;
import com.ksyun.ks3.http.HttpClientConfig;
import com.ksyun.ks3.http.HttpMethod;
import com.ksyun.ks3.service.Ks3;
import com.ksyun.ks3.service.Ks3Client;
import com.ksyun.ks3.service.Ks3ClientConfig;
import com.ksyun.ks3.service.common.BucketType;
import com.ksyun.ks3.service.request.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

/**
 * Function: 金山云对象存储工具类
 * https://docs.ksyun.com/documents/963
 *
 * @author miluo
 * Date: 2021/5/27 17:13
 * @since JDK 1.8
 */
public class KsOssUtil {
    private static KsOssUtil ksOssUtil;
    private static Ks3Client client;
    //    private final String ak = "AKLTMrohfR2HT1e91vHJFNQiXw";
    //    private final String sk = "OJWIWhdKMqXSYrWJFGS0lJNe1RHHGCiiVKfBOr8jnuhRhLByLmAw+4dA8HiAbWV5mA==";

    private KsOssUtil() {
    }

    public static void init(String ak, String sk) {
        if (ksOssUtil == null) {
            Ks3ClientConfig config = new Ks3ClientConfig();
            config.setEndpoint("ks3-cn-beijing.ksyun.com");
            config.setDomainMode(false);
            config.setProtocol(Ks3ClientConfig.PROTOCOL.https);
            config.setPathStyleAccess(false);
            HttpClientConfig httpConfig = new HttpClientConfig();
            //在HttpClientConfig中可以设置httpclient的相关属性，比如代理，超时，重试等。
            config.setHttpClientConfig(httpConfig);
            client = new Ks3Client(ak, sk, config);
            ksOssUtil = new KsOssUtil();
        }
    }

    public static KsOssUtil getInstance() {
        return ksOssUtil;
    }

    /**
     * 将new File("<filePath>")这个文件上传至<bucket名称>这个存储空间下，并命名为<key>
     *
     * @param bucket
     * @param pathKey
     * @param file
     */
    public void putObject(String bucket, String pathKey, File file) {
        PutObjectRequest request = new PutObjectRequest(bucket, pathKey, file);
        // 上传一个公开文件
        // request.setCannedAcl(CannedAccessControlList.PublicRead);
        client.putObject(request);
    }

    /**
     * 如果用户对KS3协议不是特别清楚,建议使用该方法。每次上传的时候都去获取一次最新的签名信息
     */
    public PostObjectFormFields postObjectSimple() {
        /*
         * 需要用户在postData和unknowValueField中提供所有的除KSSAccesxsKeyId, signature, file, policy外的所有表单项。否则用生成的签名上传会返回403</br>
         * 对于用户可以确定表单值的放在 postData中，对于用户无法确定表单值的放在unknownValueField中(比如有的上传控件会添加一些表单项,但表单项的值可能是随机的)</br>
         *
         */
        Map<String, String> postData = new HashMap<String, String>();
        //如果使用js sdk上传的时候设置了ACL，请提供以下一行，且值要与SDK中一致，否则删除下面一行代码
        postData.put("acl", "public-read");
        //提供js sdk中的key值
        postData.put("key", "20150115/中文/${filename}");
        List<String> unknowValueField = new ArrayList<String>();
        //js sdk上传的时候会自动加上一个name的表单项，所以下面需要加上这样的代码。
        unknowValueField.add("name");
        //如果计算签名时提供的key里不包含${filename}占位符，可以把第二个参数传一个空字符串。因为计算policy需要的key是把${filename}进行替换后的key。
        PostObjectFormFields fields = client.postObject("test-data", "demo.jpeg", postData, unknowValueField);
        fields.getKssAccessKeyId();
        fields.getPolicy();
        fields.getSignature();
        return fields;
    }

    public void getObject() {
       /* //生成一个在1000秒后过期的外链
        client.generatePresignedUrl( < bucket >,<key >, 1000);
        //生成一个1000秒后过期并重写返回的heade的外链
        ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
        //overrides.setContentType("text/html");
        //.......
        client.generatePresignedUrl( < bucket >,<key >, 1000, overrides);*/
    }

    /**
     * 获取所有bucket
     *
     * @return
     */
    public List<Bucket> listBuckets() {
        List<Bucket> buckets = client.listBuckets();
        for (Bucket bucket : buckets) {
            //获取bucket的创建时间
            bucket.getCreationDate();
            //获取bucket的名称
            bucket.getName();
            //获取bucket的拥有者（用户ID base64后的值）
            bucket.getOwner();
            //获取bucket的区域信息
            bucket.getRegion();
            //获取bucket类型，非归档（BucketType.Normal）、归档（BucketType.Archive）
            bucket.getType();
        }
        return buckets;
    }

    /**
     * 使用最简单的方式创建一个bucket
     * 将使用默认的配置，权限为私有，存储地点为北京
     */
    public Bucket createBucket(String bucketName) {
        return client.createBucket(bucketName);
    }

    /**
     * 新建bucket的时候配置bucket的存储地点和访问权限
     */
    public Bucket createBucketWithConfig(String bucketName) {
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        //配置bucket的存储地点
        CreateBucketConfiguration config = new CreateBucketConfiguration(CreateBucketConfiguration.REGION.BEIJING);
        request.setConfig(config);
        //配置bucket类型，非归档（BucketType.Normal）、归档（BucketType.Archive）。默认为非归档。
        request.setBucketType(BucketType.Normal);
        //配置bucket的访问权限
        request.setCannedAcl(CannedAccessControlList.Private);
        //执行操作
        return client.createBucket(request);
    }

    /**
     * 通过预设的ACL设置bucket的访问权限，预设的ACL包括:private:私有。public-read:为所有用户授予read权限。public-read-write:为所有用户授予read和write权限，
     *
     * @param bucket  bucket name
     * @param control CannedAccessControlList.Private
     */
    public void putBucketAclWithCannedAcl(String bucket, CannedAccessControlList control) {
        PutBucketACLRequest request = new PutBucketACLRequest(bucket);
        //设为私有
//        request.setCannedAcl(CannedAccessControlList.Private);
        //设为公开读
        //request.setCannedAcl(CannedAccessControlList.PublicRead);
        //设为公开读写
        //request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        request.setCannedAcl(control);
        client.putBucketACL(request);
    }

    /**
     * 自定义bucket的访问权限。
     */
    public void putBucketAclWithAcl(String bucketName) {
        PutBucketACLRequest request = new PutBucketACLRequest(bucketName);
        AccessControlList acl = new AccessControlList();
        //设置用户id为12345678的用户对bucket的读权限
        Grant grant1 = new Grant();
        grant1.setGrantee(new GranteeId("12345678"));
        grant1.setPermission(Permission.Read);
        acl.addGrant(grant1);

        //设置用户id为123456789的用户对bucket完全控制
        Grant grant2 = new Grant();
        grant2.setGrantee(new GranteeId("123456789"));
        grant2.setPermission(Permission.FullControl);
        acl.addGrant(grant2);

        //设置用户id为12345678910的用户对bucket的写权限
        Grant grant3 = new Grant();
        grant3.setGrantee(new GranteeId("12345678910"));
        grant3.setPermission(Permission.Write);
        acl.addGrant(grant3);
        request.setAccessControlList(acl);
        client.putBucketACL(request);
    }


    /**
     * 获取bucket访问权限
     *
     * @param bucketName bucket name
     * @return
     */
    public AccessControlPolicy getBucketAcl(String bucketName) {
        AccessControlPolicy acl = null;
        //只需要传入这个bucket的名称即可
        acl = client.getBucketACL(bucketName);
        return acl;
    }

    /**
     * 将存储空间的操作日志存储在 <存放日志文件的bucket名称> 里面，日志文件的前缀是"logging-"
     *
     * @param bucketName    bucket name
     * @param logBucketName 日志存放的bucket name
     */
    public void putBucketLogging(String bucketName, String logBucketName) {
        PutBucketLoggingRequest request = new PutBucketLoggingRequest(bucketName);
        //开启日志
        request.setEnable(true);
        request.setTargetBucket(logBucketName);
        //设置日志文件的前缀为logging-
        request.setTargetPrefix("logging-");
        client.putBucketLogging(request);
    }

    /**
     * 获取bucket的日志配置
     *
     * @param bucketName
     * @return
     */
    public BucketLoggingStatus getBucketLogging(String bucketName) {
        //只需要传入bucket的名称，由于ks3暂时对日志权限不支持，所以返回的BucketLoggingStatus中targetGrants始终为空集合
        BucketLoggingStatus logging = client.getBucketLogging(bucketName);
        return logging;
    }


    /**
     * Head请求一个bucket
     */

    public HeadBucketResult headBucket(String bucketName) {
        HeadBucketResult result = client.headBucket(bucketName);
        // 通过result.getStatueCode()状态码 404则这个bucket不存在，403当前用户没有权限访问这个bucket
        return result;
    }

    /**
     * 获取bucket region
     *
     * @param bucketName
     * @return
     */
    public CreateBucketConfiguration.REGION getBucketLocation(String bucketName) {
        //只需要传入bucket的名称
        CreateBucketConfiguration.REGION region = client.getBucketLoaction(bucketName);
        return region;
    }


    /**
     * 删除bucket的CORS跨域规则
     *
     * @param bucketName
     */
    public void deleteBucketCors(String bucketName) {
        client.deleteBucketCors(bucketName);
    }

    /**
     * 删除bucket
     *
     * @param bucketName
     */
    public void deleteBucket(String bucketName) {
        client.deleteBucket(bucketName);
    }

    /**
     * 列出一个bucket下的object，返回的最大数为1000条
     *
     * @param bucketName
     * @return
     */
    public ObjectListing listObjectsSimple(String bucketName) {
        ObjectListing list = client.listObjects(bucketName);
        return list;

    }

    /**
     * 将列出bucket下满足object key前缀为指定字符串的object，返回的最大数为1000条
     */
    public List<ObjectListing> listObjectsWithPrefix(String bucketName, String prefixObjectKey) {
        return Collections.singletonList(client.listObjects(bucketName, prefixObjectKey));
    }

    /**
     * 自己调节列出object的参数，
     */
    public ObjectListing listObjectsUseRequest(String bucketName, int keyNum, String prefixObjectKey, String delimiter) {
        ObjectListing list = null;
        //新建一个ListObjectsRequest
        ListObjectsRequest request = new ListObjectsRequest(bucketName);
        //指定返回条数最大值
        request.setMaxKeys(keyNum);
        //返回以指定前缀开头的object
        request.setPrefix(prefixObjectKey);
        //设置文件分隔符，系统将根据该分隔符组织文件夹结构，默认是"/"
        request.setDelimiter(delimiter);
        //执行操作
        list = client.listObjects(request);
        return list;
    }

    /**
     * 使用循环列出所有object
     */
    public ObjectListing listAllObjects(String bucketName) {
        ObjectListing list = listObjectsSimple(bucketName);
        //初始化一个请求
        ListObjectsRequest request = new ListObjectsRequest(bucketName);
        do {
            //isTruncated为true时表示之后还有object，所以应该继续循环
            if (list != null && list.isTruncated()) {
                //在ObjectListing中将返回下次请求的marker
                //如果请求的时候没有设置delimiter，则不会返回nextMarker,需要使用上一次list的最后一个key做为nextMarker
            }
            request.setMarker(list.getObjectSummaries().get(list.getObjectSummaries().size() - 1).getKey());
            list = client.listObjects(request);
        } while (list.isTruncated());
        return list;
    }

    /**
     * 上传一个文件
     *
     * @param bucketName        bucket name
     * @param objectKey         剩余路径名
     * @param file              通过File类型上传
     * @param cannedControlList
     */
    public void putObjectSimple(String bucketName, String objectKey, File file, CannedAccessControlList cannedControlList) {
        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, file);

        if (null == cannedControlList) {
            cannedControlList = CannedAccessControlList.Private;
        }
        //设置文件类型
        request.setCannedAcl(cannedControlList);
        PutObjectResult putObjectResult = client.putObject(request);
        System.out.println(putObjectResult);
    }

    /**
     * 上传一个文件
     *
     * @param bucketName        bucket name
     * @param objectKey         剩余路径名
     * @param fileName          通过文件路径上传
     * @param cannedControlList
     */
    public void putObjectSimple(String bucketName, String objectKey, String fileName, CannedAccessControlList cannedControlList) {
        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, new File(fileName));
        if (null == cannedControlList) {
            cannedControlList = CannedAccessControlList.Private;
        }
        //设置文件类型
        request.setCannedAcl(cannedControlList);
        client.putObject(request);
    }

    /**
     * 将一个输入流中的内容上传至<bucket名称>这个存储空间下，并命名为<object key>
     *
     * @param bucketName
     * @param objectKey
     * @param bis
     * @param contentLength
     */
    public void putObjectSimple(String bucketName, String objectKey, ByteArrayInputStream bis, int contentLength) {
        ObjectMetadata meta = new ObjectMetadata();
        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, bis, meta);
        // 可以指定内容的长度，否则程序会把整个输入流缓存起来，可能导致jvm内存溢出
        meta.setContentLength(contentLength);
        client.putObject(request);
    }


    public Ks3Result putObjectFetch(String bucketName, String objectKey, String url) {
        //设置需要fetch object请求的，bucket、文件名以及源站地址
        PutObjectFetchRequest request = new PutObjectFetchRequest(bucketName, objectKey, url);
        //将文件设置为公开读
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        //base64加密MD5信息，128位，用于对象完整性校验
//        request.setMd5Base64(md5);
        ObjectMetadata metadata = new ObjectMetadata();
        //设置文件的元数据
        request.setObjectMeta(metadata);
        //设置回调,fetchObject是异步操作，不会马上感知是否成功，需要指定上传成功或失败时的回调URL
//        request.setCallbackUrl(callbackurl);
        Ks3Result result = client.putObjectFetch(request);
        return result;
    }

    /**
     * 文件复制
     *
     * @param destinationBucket 目的地bucket
     * @param destinationObject 目的地object
     * @param sourceBucket      源bucket
     * @param sourceKey         源object
     * @return
     */
    public CopyResult copyObject(String destinationBucket, String destinationObject, String sourceBucket, String sourceKey) {
        //将sourceBucket这个存储空间下的sourceKey这个object复制到destinationBucket这个存储空间下，并命名为destinationObject
        CopyObjectRequest request = new CopyObjectRequest(destinationBucket, destinationObject, sourceBucket, sourceKey);
        return client.copyObject(request);
    }

    /**
     * 获取文件元数据
     *
     * @param bucket
     * @param object
     * @return
     */
    public HeadObjectResult headObject(String bucket, String object) {
        HeadObjectRequest request = new HeadObjectRequest(bucket, object);
        HeadObjectResult result = client.headObject(request);
        // head请求可以用于获取object的元数据
        result.getObjectMetadata();
        return result;
    }


    /**
     * 判断一个object是否存在
     */
    public boolean objectExists(String bucket, String object) {
        try {
            HeadObjectRequest request = new HeadObjectRequest(bucket, object);
            client.headObject(request);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }


    /**
     * 下载文件
     *
     * @param bucket
     * @param objectKey
     * @return
     */
    public GetObjectResult getObject(String bucket, String objectKey) {
        GetObjectRequest request = new GetObjectRequest(bucket, objectKey);
        //重写返回的header
        ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
        overrides.setContentType("text/html");
        request.setOverrides(overrides);
        //只接受数据的0-10字节。通过控制该项可以实现分块下载
        //request.setRange(0,10);
        GetObjectResult result = client.getObject(request);
//        Ks3Object object = result.getObject();
//        //获取object的元数据
//        ObjectMetadata meta = object.getObjectMetadata();
//        //当分块下载时获取文件的实际大小，而非当前小块的大小
//        Long length = meta.getInstanceLength();
//        //获取object的输入流
//        object.getObjectContent();
        return result;
    }

    /**
     * 将<bucket名称>这个存储空间下的<object key>删除
     *
     * @param bucket
     * @param objectKey
     */
    public void deleteObject(String bucket, String objectKey) {
        client.deleteObject(bucket, objectKey);
    }

    /**
     * 解冻<bucket名称>这个存储空间下的<object key>文件
     */
    public RestoreObjectResult restoreObject(String bucket, String objectKey) {
        RestoreObjectResult result = client.restoreObject(bucket, objectKey);
        //获取<object key>文件的存储类型，类型如下
        //标准（StorageClass.Standard）、低频（StorageClass.StandardInfrequentAccess）、归档（StorageClass.Archive）
        result.getCls();
        //获取<object key>文件的解冻状态，状态如下
        //解冻成功（RestoreCycle.RESTORE）、正在解冻中（RestoreCycle.RESTORING）、已解冻（RestoreCycle.RESTORED）
        result.getType();
        return result;
    }

    /**
     * 为已存在的对象添加tag
     *
     * @param bucket
     * @param objectName
     * @param tags
     */
    public void putObjectTagging(String bucket, String objectName, List<ObjectTag> tags) {
        ObjectTagging tagging = new ObjectTagging(tags);

        PutObjectTaggingRequest request = new PutObjectTaggingRequest(bucket, objectName, tagging);
        client.putObjectTagging(request);
    }

    /**
     * 通过外链访问文件
     *
     * @param bucket
     * @param objectName
     * @return
     */
    public String shiftName(String bucket, String objectName) {
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest();
        req.setMethod(HttpMethod.GET);
        req.setBucket(bucket);
        req.setKey(objectName);
        //unix时间戳，不指定的话则默认为15分钟后过期
        Calendar nowTime = Calendar.getInstance();
        //30分钟后的时间
        nowTime.add(Calendar.DATE, 1);
        req.setExpiration(nowTime.getTime());
        ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
        //overrides.setContentType("application/xml");//设置返回的Content-Type
        req.setResponseHeaders(overrides);
        return client.generatePresignedUrl(req);
    }

    /**
     * 通过外链上传
     * 需要查阅金山云oss文档 java sdk 9.2节内容
     *
     * @param bucket
     * @param objectName
     * @return
     */
    public String upload(String bucket, String objectName) {
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest();
        req.setMethod(HttpMethod.PUT);
        req.setBucket(bucket);//文件上传的bucket
        req.setKey(objectName);//文件名
        //req.setExpiration(<生成的外链的过期时间>);//不指定的话则默认为15分钟后过期
        req.getRequestConfig().getExtendHeaders().put("x-kss-acl", "public-read");//设置acl为公开读，不加该header则默认为私有，生成外链时设置了header，则在使用外链的时候也需要添加相应的header
        req.setContentType("video/mp4");//设置文件的Content-Type,具体值请根据时间情况设定。在使用外链的时候需要把Content-Type设置成指定的值
        //req.setSseAlgorithm("AES256");//设置服务端加密
        return client.generatePresignedUrl(req);
    }


}
