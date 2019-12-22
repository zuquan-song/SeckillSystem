package org.seckill.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.seckill.result.CodeMsg;
import org.seckill.result.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Zuquan Song
 *
 * @description GlobalExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exception(HttpServletRequest request, Exception e) {

        e.printStackTrace();

        if (e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            CodeMsg codeMsg = globalException.getCm();
            return Result.error(codeMsg);
        }
        if (e instanceof BindException) {
            BindException bindException = (BindException) e;
            List <ObjectError> allErrors = bindException.getAllErrors();
            ObjectError error = allErrors.get(0);
            String defaultMessage = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(defaultMessage));
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
