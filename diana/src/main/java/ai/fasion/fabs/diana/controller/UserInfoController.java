package ai.fasion.fabs.diana.controller;

import ai.fasion.fabs.diana.common.PageRequestInfo;
import ai.fasion.fabs.diana.domain.dto.UserInfoDTO;
import ai.fasion.fabs.diana.domain.po.UserInfoPO;
import ai.fasion.fabs.diana.domain.pojo.PageRequest;
import ai.fasion.fabs.diana.domain.pojo.ResponseEmptyEntity;
import ai.fasion.fabs.diana.domain.vo.AllInfoVO;
import ai.fasion.fabs.diana.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api("注册用户管理")
@RestController
@RequestMapping("/users")
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final PageRequestInfo pageRequestInfo;

    public UserInfoController(UserInfoService userInfoService, PageRequestInfo pageRequestInfo) {
        this.userInfoService = userInfoService;
        this.pageRequestInfo = pageRequestInfo;
    }

    @ApiOperation("获取用户列表")
    @GetMapping
    public ResponseEntity<Object> selectAllUserInfo(@RequestParam(value = "phone", required = false) String phone, @RequestParam(value = "uid", required = false) String uid,
                                                    @RequestParam(value = "page", required = false) Integer page) {
        PageRequest pageRequest = pageRequestInfo.pageRequest(page);
        AllInfoVO allUserInfo = userInfoService.selectAll(phone, uid, pageRequest);
        return new ResponseEntity<>(allUserInfo, HttpStatus.OK);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/{uid}")
    public ResponseEntity<Object> findUserInfoById(@PathVariable(value = "uid", required = true) String uid) {
        UserInfoPO userInfoPO = userInfoService.findById(uid);
        if (null == userInfoPO) {
            return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.NOT_FOUND);
        }
        Map<String, Object> map = new HashMap<>(4);
        map.put("uid", userInfoPO.getId());
        map.put("nickname", userInfoPO.getNickname());
        map.put("avatar", userInfoPO.getAvatar());
        map.put("phone", userInfoPO.getPhone());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @ApiOperation("修改用户信息")
    @PatchMapping
    public ResponseEntity<Object> updateUserInfo(@Validated @RequestBody UserInfoDTO userInfoDTO) {
        Map map = userInfoService.updateUserInfo(userInfoDTO);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }



}
