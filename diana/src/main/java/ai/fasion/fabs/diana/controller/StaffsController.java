package ai.fasion.fabs.diana.controller;

import ai.fasion.fabs.diana.domain.vo.StaffsVO;
import ai.fasion.fabs.diana.interceptor.AppThreadLocalHolder;
import ai.fasion.fabs.diana.service.StaffsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("后台管理员")
@RestController
@RequestMapping("/staffs")
public class StaffsController {

    private final StaffsService staffsService;

    public StaffsController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @ApiOperation("获取管理员信息")
    @GetMapping("/me")
    public ResponseEntity<Object> staffsInfo(){
        //获取管理员id
        String id = AppThreadLocalHolder.getUserId();
        //通过id查询管理员基本信息
        StaffsVO staffsVO = staffsService.staffsInfo(id);
        return new ResponseEntity<>(staffsVO, HttpStatus.OK);
    }
}
