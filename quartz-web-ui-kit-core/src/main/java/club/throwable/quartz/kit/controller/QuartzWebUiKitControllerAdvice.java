package club.throwable.quartz.kit.controller;

import club.throwable.quartz.kit.exception.QuartzWebUiKitException;
import club.throwable.quartz.kit.service.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 23:02
 */
@Slf4j
@RestControllerAdvice
public class QuartzWebUiKitControllerAdvice {

    @ExceptionHandler(value = QuartzWebUiKitException.class)
    public Response<?> handleQuartzWebUiKitException(HttpServletRequest request, QuartzWebUiKitException e) {
        log.error("请求命中任务处理异常,URI:{},客户端IP:{}", request.getRequestURI(), request.getRemoteAddr(), e);
        Response<?> response = new Response<>();
        response.setCode(Response.ERROR);
        response.setMessage(e.getMessage());
        return response;
    }
}
