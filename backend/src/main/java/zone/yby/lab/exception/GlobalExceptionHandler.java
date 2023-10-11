package zone.yby.lab.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zone.yby.lab.utils.ResponseResult;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult<String> handleException(Exception e) {
        e.printStackTrace();
        return new ResponseResult<>(500, e.getMessage());
    }
}