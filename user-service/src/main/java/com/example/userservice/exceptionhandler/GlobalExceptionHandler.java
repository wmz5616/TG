package com.example.userservice.exceptionhandler;

import com.example.userservice.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // 声明这是一个全局异常处理组件
public class GlobalExceptionHandler {

    // 这个方法专门捕获我们自定义的 UsernameAlreadyExistsException 异常
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        // 当捕获到该异常时，返回 409 Conflict 状态码和异常信息
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
