package ai.fasion.fabs.diana.controller;


import ai.fasion.fabs.diana.domain.dto.AdminUserDTO;
import ai.fasion.fabs.diana.domain.pojo.ResponseEmptyEntity;
import ai.fasion.fabs.diana.domain.vo.AdminUserVO;
import ai.fasion.fabs.diana.service.AdminUserService;
import ai.fasion.fabs.vesta.expansion.AuthorizationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("登录和验证")
@RestController
@RequestMapping("/auth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @ApiOperation("系统登录接口")
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AdminUserDTO adminUserDTO) {
        AdminUserVO adminUserVO = adminUserService.login(adminUserDTO);
        if (null == adminUserVO) {
            throw new AuthorizationException("用户名或密码错误");
        }
        return new ResponseEntity<>(adminUserVO, HttpStatus.OK);
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public ResponseEntity<Object> logout (){
        adminUserService.logout();
        return new ResponseEntity<>(ResponseEmptyEntity.getInstance(), HttpStatus.OK);
    }
}
