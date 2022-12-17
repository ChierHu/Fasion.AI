package ai.fasion.fabs.diana.service.impl;

import ai.fasion.fabs.diana.common.ReturnFormat;
import ai.fasion.fabs.diana.domain.dto.PayloadDTO;
import ai.fasion.fabs.diana.domain.po.TaskPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.domain.vo.TaskVO;
import ai.fasion.fabs.diana.mapper.TaskMapper;
import ai.fasion.fabs.diana.service.TaskService;
import ai.fasion.fabs.vesta.enums.Task;
import ai.fasion.fabs.vesta.expansion.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    private final ObjectMapper objectMapper;

    private final ReturnFormat returnFormat;

    public TaskServiceImpl(TaskMapper taskMapper, ObjectMapper objectMapper, ReturnFormat returnFormat) {
        this.taskMapper = taskMapper;
        this.objectMapper = objectMapper;
        this.returnFormat = returnFormat;
    }

    @Override
    public AllInfoVO findAll(String ownerId, PageRequest pageRequest, String type) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<TaskPO> list = null;
        if (null == type || type.isEmpty()) {
            //type传给枚举Task.Type.taskTypeOf(type)时值不能为空，如果为空报错。
            list = taskMapper.selectByType(ownerId, null);
        } else {
            list = taskMapper.selectByType(ownerId, Task.Type.taskTypeOf(type).getCode());
        }
        AllInfoVO allInfoVO = returnFormat.format(list);

        List<TaskVO> taskVOList = new ArrayList<>(list.size());
        list.forEach(item -> {
            //查询到的任务list赋值到TaskVO返现给前端
            TaskVO taskVO = new TaskVO();
            taskVO.setId(item.getId());
            taskVO.setType(item.getType().getLabel());
            taskVO.setStatus(item.getStatus().getLabel());
            taskVO.setUid(item.getOwnerId());
            taskVO.setOwner(item.getOwner());
            taskVO.setPtLogId(item.getPtLogId());
            taskVO.setStartedAt(item.getStartedAt());
            taskVO.setFinishedAt(item.getFinishedAt());
            taskVO.setCreatedAt(item.getCreatedAt());
            taskVO.setUpdatedAt(item.getUpdatedAt());
            taskVOList.add(taskVO);
        });
        allInfoVO.setData(taskVOList);
        return allInfoVO;
    }

    @Override
    public Map<String, Object> findByTaskId(String taskId) {

        //任务id筛选任务数据
        TaskPO taskPO = taskMapper.findByTaskId(taskId);
        if (null == taskPO) {
            throw new NotFoundException("任务不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", taskPO.getId());
        map.put("type", taskPO.getType().getLabel());
        map.put("status", taskPO.getStatus().getLabel());
        if (taskPO.getDetails() != null) {
            //类型不一样，返回的样式也会不一样
            if (Task.Type.taskTypeOf(taskPO.getType().getLabel()).equals(Task.Type.MattingImage)) {
                try {
                    Task.MattingOutput payloadDTO = objectMapper.readValue(taskPO.getDetails(), Task.MattingOutput.class);
                    map.put("details", payloadDTO);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else if (Task.Type.taskTypeOf(taskPO.getType().getLabel()).equals(Task.Type.FaceSwap)) {
                try {
                    Task.FaceSwapInfo payloadDTO = objectMapper.readValue(taskPO.getDetails(), Task.FaceSwapInfo.class);
                    map.put("details", payloadDTO);
                } catch (JsonProcessingException e) {
                }
            }
        }
        try {
            List<PayloadDTO> payloadDTOS = objectMapper.readValue(taskPO.getPayload(), new TypeReference<List<PayloadDTO>>() {
            });
            map.put("payload", payloadDTOS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }
}
