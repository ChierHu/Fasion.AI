package ai.fasion.fabs.apollo.tasks;

import ai.fasion.fabs.apollo.assets.vo.AssetPahtVO;
import ai.fasion.fabs.apollo.constant.KsOssConstant;
import ai.fasion.fabs.apollo.tasks.dto.CreateRequestDTO;
import ai.fasion.fabs.apollo.tasks.dto.PayloadDTO;
import ai.fasion.fabs.apollo.tasks.po.TaskPO;
import ai.fasion.fabs.apollo.domain.PageRequest;
import ai.fasion.fabs.apollo.assets.AssetMapper;
import ai.fasion.fabs.apollo.tasks.vo.*;
import ai.fasion.fabs.vesta.enums.Task;
import ai.fasion.fabs.vesta.enums.UserTypeEnum;
import ai.fasion.fabs.vesta.expansion.BadRequestException;
import ai.fasion.fabs.vesta.expansion.FailException;
import ai.fasion.fabs.vesta.expansion.NotFoundException;
import ai.fasion.fabs.vesta.service.context.AppThreadLocalHolder;
import ai.fasion.fabs.vesta.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ksyun.ks3.dto.CannedAccessControlList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Service
public class TasksServiceImpl implements TasksService {
    private static final Logger log = LoggerFactory.getLogger(TasksServiceImpl.class);

    /**
     * 文件临时下载地址
     */
    @Value("${apollo.download.workdir}")
    private String fileUrl;

    @Value("${apollo.task.url}")
    private String domainUrl;

    private final TaskMapper taskMapper;
    private final AssetMapper assetMapper;
    private final KsOssConstant ksOssConstant;
    private final ObjectMapper objectMapper;

    public TasksServiceImpl(TaskMapper taskMapper, AssetMapper assetMapper, KsOssConstant ksOssConstant, ObjectMapper objectMapper) {
        this.taskMapper = taskMapper;
        this.assetMapper = assetMapper;
        this.ksOssConstant = ksOssConstant;
        this.objectMapper = objectMapper;
    }

    @Override
    public AllTaskInfoVO findAll(PageRequest pageRequest, String uid, String type, String since, String until) {
        AllTaskInfoVO allTaskInfoVO = new AllTaskInfoVO();
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        PageInfo<TaskPO> pageInfo = new PageInfo<>(taskMapper.selectByType(uid, Task.Status.Pending.getCode(), Task.Status.Claimed.getCode(), Task.Status.Succeed.getCode(), Task.Status.Stopped.getCode(), Task.Type.taskTypeOf(type).getCode(), since, until));
        allTaskInfoVO.setTotal((int) pageInfo.getTotal());
        LinkVO linkVO = new LinkVO();
        //判断当前页是否是最后一页
        if (pageInfo.getPageNum() < pageInfo.getPages()) {
            linkVO.setNext("/tasks?page=" + (pageInfo.getPageNum() + 1));
        }
        linkVO.setLast("/tasks?page=" + pageInfo.getPages());
        allTaskInfoVO.setLinks(linkVO);

        List<TaskVO> taskVOList = new ArrayList<>(pageInfo.getSize());
        pageInfo.getList().forEach(item -> {
            TaskVO taskVO = new TaskVO();
            taskVO.setId(item.getId());
            taskVO.setType(item.getType().getLabel());
            taskVO.setStatus(item.getStatus().getLabel());
            taskVO.setCreatedAt(item.getCreatedAt());
            if (item.getDetails() != null) {
                if (Task.Type.taskTypeOf(type).equals(Task.Type.FaceSwap)) {
                    log.info("details is [FaceSwap]");
                    try {
                        Task.FaceSwapInfo payloadDTO = objectMapper.readValue(item.getDetails(), Task.FaceSwapInfo.class);
                        taskVO.setDetails(payloadDTO);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        log.error("type is [FaceSwap] -> details error:{}", e.getMessage());
                    }
                } else if (Task.Type.taskTypeOf(type).equals(Task.Type.MattingImage)) {
                    log.info("details is [MattingImage]");
                    try {
                        Task.MattingOutput payloadDTO = objectMapper.readValue(item.getDetails(), Task.MattingOutput.class);
                        taskVO.setDetails(payloadDTO);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        log.error("type is [MattingImage] -> details error:{}", e.getMessage());
                    }

                }
            }
            try {
                List<PayloadDTO> payloadDTOS = objectMapper.readValue(item.getPayload(), new TypeReference<List<PayloadDTO>>() {
                });
                taskVO.setPayload(payloadDTOS);
            } catch (JsonProcessingException e) {
                log.error("payload error:{}", e.getMessage());
            }
            taskVOList.add(taskVO);
        });
        allTaskInfoVO.setData(taskVOList);
        return allTaskInfoVO;
    }

    @Override
    public Map<String, Object> findByTaskId(String uid, String taskId) {
        TaskPO taskPO = taskMapper.findByTaskId(uid, Task.Status.Removed.getCode(), taskId);
        if (null == taskPO) {

            log.error("任务不存在，uid->[{}] taskId->[{}]", uid, taskId);
            throw new NotFoundException("任务不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", taskPO.getId());
        map.put("type", taskPO.getType().getLabel());
        map.put("status", taskPO.getStatus().getLabel());
        if (taskPO.getDetails() != null) {
            if (Task.Type.taskTypeOf(taskPO.getType().getLabel()).equals(Task.Type.MattingImage)) {
                log.info("details is [MattingImage]");
                try {
                    Task.MattingOutput payloadDTO = objectMapper.readValue(taskPO.getDetails(), Task.MattingOutput.class);
                    map.put("details", payloadDTO);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    log.error("type is [FaceSwap] uid->[{}] taskId->[{}] details error:{}", uid, taskId, e.getMessage());
                }
            } else if (Task.Type.taskTypeOf(taskPO.getType().getLabel()).equals(Task.Type.FaceSwap)) {
                log.info("details is [FaceSwap]");
                try {
                    Task.FaceSwapInfo payloadDTO = objectMapper.readValue(taskPO.getDetails(), Task.FaceSwapInfo.class);
                    map.put("details", payloadDTO);
                } catch (JsonProcessingException e) {
                    log.error("type is [MattingImage] uid->[{}] taskId->[{}] details error:{}", uid, taskId, e.getMessage());
                }
            }
        }
        try {
            List<PayloadDTO> payloadDTOS = objectMapper.readValue(taskPO.getPayload(), new TypeReference<List<PayloadDTO>>() {
            });
            map.put("payload", payloadDTOS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("payload error:{}", e.getMessage());
        }
        return map;
    }

    @Override
    public SubmitTasksVO submitTask(String type, TasksPayloadVO payload) throws JsonProcessingException {
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CreateRequestDTO<List<PayloadDTO>> createRequestDTO = new CreateRequestDTO<>();
        createRequestDTO.setOwnerType(UserTypeEnum.USER.getName());
        createRequestDTO.setOwnerId(String.valueOf(AppThreadLocalHolder.getUserId()));
        createRequestDTO.setType(type);
        //json转list
        List<PayloadDTO> payloads = objectMapper.readValue(objectMapper.writeValueAsString(payload.getPayload()), new TypeReference<List<PayloadDTO>>() {
        });

        payloads.forEach(item -> {
            boolean specialChar = KnifeTool.isSpecialChar(item.getSource() + item.getTarget());
            if (specialChar) {
                throw new BadRequestException("提交的信息不符合要求！");
            }
        });

        createRequestDTO.setPayload(payloads);
        String url = domainUrl + "tasks";
        HttpEntity<CreateRequestDTO> entity = new HttpEntity<>(createRequestDTO, headers);
        ResponseEntity<String> responseEntity = RestTemplateUtils.post(url, entity, String.class);
        if (responseEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new FailException("系统错误");
        }

        String result = responseEntity.getBody();
        log.info("提交任务到minerva模块后，返回的结果为{}", result);
        SubmitTasksVO submitTasksVO = objectMapper.readValue(result, SubmitTasksVO.class);
        submitTasksVO.setPoint(Math.abs(submitTasksVO.getPoint()));
        return submitTasksVO;
    }

    @Override
    public ZipVO downloadTasks(String id) {
        // 拼接path
        String dir =
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                        + File.separator
                        + "FasionAI_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "_" + id;
        //创建下载文件地址
        String urls = fileUrl + "/" + dir;
        File file = new File(urls);
        file.mkdirs();
        log.info("下载路径{}", urls);
        //删除原有文件夹下之前的文件
        deleteDir(urls);
        //先获取path路径
        List<AssetPahtVO> assetPahtVO = assetMapper.findPathByBundle(id);
        assetPahtVO.forEach(assetPath -> {
            //截取文件的后缀
            String suffix = assetPath.getPath().substring(assetPath.getPath().lastIndexOf("."));
            //通过金山云查询path目录下的所有文件
            String url = KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), assetPath.getPath());
            //把文件下载到服务器上
            DownloadUtil.downloadFile(url, urls + "/" + assetPath.getId() + suffix);
        });

        //把下载后的文件夹进行压缩
        return returnZipUrl(urls, dir);
    }

    @Override
    public ResponseEntity<String> checkout(String uid, String taskId, String purchaseId) {
        String url = domainUrl + "/tasks/" + taskId + "/checkout?uid=" + uid + "&purchaseId=" + purchaseId;
        try {
            ResponseEntity<String> responseEntity = RestTemplateUtils.post(url, String.class);
            return new ResponseEntity<>("执行成功", HttpStatus.OK);
        } catch (Exception e) {
            String text = e.getMessage();
            text = text.substring(text.lastIndexOf("[")).replace("]", "").replace("[", "");
            return new ResponseEntity<>(text, HttpStatus.PAYMENT_REQUIRED);
        }
    }

    private ZipVO returnZipUrl(String urls, String dir) {
        //把下载后的文件夹进行压缩
        //创建压缩包文件以及地址
        String zipUrl = fileUrl + "/" + dir;
        FileOutputStream fos1 = null;
        try {
            fos1 = new FileOutputStream(zipUrl + ".zip");
            new ZipOutputStream(fos1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZipUtils.toZip(urls, fos1, true);
        //上传到金山云 并且返回路径
        // 获取文件文件名
        String originalFilename = zipUrl.substring(0, zipUrl.lastIndexOf("/"));

        //把压缩包上传到金山云
        KsOssUtil.getInstance().putObjectSimple(ksOssConstant.getBucketName(), "zip/" + dir + ".zip", zipUrl + ".zip", CannedAccessControlList.PublicRead);
        //上传后的地址为
        //zip文档上传到金山云的路径
        String zipPath = "zip/" + dir + ".zip";
        ZipVO zipVO = new ZipVO();
        zipVO.setLink(KsOssUtil.getInstance().shiftName(ksOssConstant.getBucketName(), zipPath));
        return zipVO;
    }


    /**
     * 删除一个文件夹中的所有的文件
     *
     * @param path
     * @return
     */
    public static boolean deleteDir(String path) {
        File file = new File(path);
        //判断是否待删除目录是否存在
        if (!file.exists()) {
            log.error("The dir are not exists! -> {}", path);
            return false;
        }

        //取得当前目录下所有文件和文件夹
        String[] content = file.list();
        assert content != null;
        for (String name : content) {
            File temp = new File(path, name);
            //判断是否是目录
            if (temp.isDirectory()) {
                //递归调用，删除目录里的内容
                boolean isDelete = deleteDir(temp.getAbsolutePath());
                //删除空目录
                isDelete = temp.delete();
            } else {
                //直接删除文件
                if (!temp.delete()) {
                    log.error("Failed to delete -> {}", name);
                }
            }
        }
        return true;
    }
}
