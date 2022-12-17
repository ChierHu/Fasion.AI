package ai.fasion.fabs.apollo.config;

import ai.fasion.fabs.apollo.domain.Failure;
import ai.fasion.fabs.vesta.common.ResultCode;
import ai.fasion.fabs.vesta.expansion.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Set;

/**
 * Function: 全局异常处理类,RestControllerAdvice负责及时发送消息
 *
 * @author miluo Date: 2018/9/6 下午4:28
 * @since JDK 1.8
 */
@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    /**
     * 根据Validated注解，拦截在请求中的参数
     *
     * @param exception 异常信息集合
     * @return 结果集
     */
    @ExceptionHandler
    public ResponseEntity<Failure> handle(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            sb.append(violation.getMessage()).append("\n");
        }
        return new ResponseEntity<>(new Failure(sb.toString()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 拦截 Request method 'POST' not supported
     *
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Failure> httpRequestMethodNotSupportedException() {
        return new ResponseEntity<>(new Failure("请检查HTTP方法类型是否正确！"), HttpStatus.BAD_REQUEST);
    }

    /**
     * 授权异常
     *
     * @return 返回401 状态码
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Failure> authorizationException(AuthorizationException authorizationException) {
        return new ResponseEntity<>(new Failure(authorizationException.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * 捕捉参数验证异常
     *
     * @param e 异常类型
     * @return 结果集
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Failure> validateException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(new Failure(e.getMessage().substring(e.getMessage().lastIndexOf('[') + 1, e.getMessage().length() - 3)), HttpStatus.FORBIDDEN);
    }

    /**
     * 解密未验证通过
     *
     * @param exception
     * @return
     */
    @ExceptionHandler
    public ResponseEntity<Failure> encryptRequestException(EncryptRequestException exception) {
        return new ResponseEntity<>(new Failure("解密未通过验证：" + exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * 请求过来没有传参数，或者参数不够数量
     *
     * @return 返回4000 状态码
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Failure> parameterException() {
        return new ResponseEntity<>(new Failure("请求的参数不完整！"), HttpStatus.BAD_REQUEST);
    }


    /**
     * 请求过来没有传参数，或者参数不够数量
     *
     * @return 返回4000 状态码
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Failure> httpMessageNotReadableException() {
        return new ResponseEntity<>(new Failure("HTTP参数有误(无参或少传)"), HttpStatus.BAD_REQUEST);
    }

    /**
     * 未知主机异常
     *
     * @return 返回503
     */
    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<Failure> unknownHostException(UnknownHostException e) {
        return new ResponseEntity<>(new Failure(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 自定义异常 400
     *
     * @return
     */
    @ExceptionHandler(FailException.class)
    public ResponseEntity<Failure> failException(FailException e) {
        return new ResponseEntity<>(new Failure(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 自定义异常 400
     *
     * @return
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Failure> badRequestException(BadRequestException e) {
        return new ResponseEntity<>(new Failure(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 未发现异常 404
     *
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Failure> notFoundException(NotFoundException e) {
        return new ResponseEntity<>(new Failure(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * 空指针异常
     *
     * @return 返回5005
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Failure> nullPointerException(NullPointerException e) {
        return new ResponseEntity<>(new Failure(ResultCode.RESPONSE_RESULT_ERROR.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 文件不存在异常
     *
     * @return 返回4554
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Failure> fileNotFoundException(IOException e) {
        return new ResponseEntity<>(new Failure(ResultCode.FILE_NOT_FOUND.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 文件处理异常
     *
     * @return 返回4449
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Failure> ioException(IOException e) {
        if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(e), "Broken pipe")) {
            return null;
        } else {
            return new ResponseEntity<>(new Failure(ResultCode.FILE_UPLOAD_ERROR.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * 时间转换异常
     *
     * @return 返回4559
     */
    @ExceptionHandler(ParseException.class)
    public ResponseEntity<Failure> parseException(ParseException e) {
        return new ResponseEntity<>(new Failure(ResultCode.PARAM_NOT_COMPLETE.getMessage()), HttpStatus.BAD_REQUEST);
    }


    /**
     * 文件超过限制
     *
     * @param e
     * @return
     */
    @ExceptionHandler({FileSizeLimitExceededException.class, IllegalStateException.class})
    public ResponseEntity<Failure> fileSizeLimitExceededException(FileSizeLimitExceededException e) {
        return new ResponseEntity<>(new Failure("上传的文件超过限制的大小!"), HttpStatus.BAD_REQUEST);
    }

    // =========================================全局异常拦截方法如下======================================================

    /**
     * 捕捉其他所有异常
     *
     * @param request 请求信息
     * @param ex      异常类型
     * @return 结果集
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Failure> globalException(HttpServletRequest request, Throwable ex) {
        ex.printStackTrace();

//        if ("prod".equalsIgnoreCase(description)) {
//            return ResultGenerator.genNeutralResult(getStatus(request).value(), "暂时不能为您提供服务，请联系管理员！");
//        } else {
//            return ResultGenerator.genNeutralResult(getStatus(request).value(), ex.getMessage());
//        }
        return new ResponseEntity<>(new Failure(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 获取java给我们提供的未被拦截的异常状态码
     *
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
