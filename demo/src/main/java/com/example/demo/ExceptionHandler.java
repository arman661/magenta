package com.example.demo;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@ResponseBody
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({CannotBeCalculatedException.class})
    public Object processException(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            CannotBeCalculatedException ex
            ){
        return ex.getMessage();
    }
}
