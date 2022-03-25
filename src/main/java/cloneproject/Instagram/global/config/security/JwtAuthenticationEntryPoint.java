package cloneproject.Instagram.global.config.security;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.ErrorResponse;
import cloneproject.Instagram.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                    AuthenticationException authException) throws IOException {
        Object errorObject = request.getAttribute("bussinessException");
        if(errorObject != null){
            if(errorObject instanceof BusinessException){
                ErrorCode errorCode = ((BusinessException) errorObject).getErrorCode();
                sendError(response, errorCode);
            }else{
                sendError(response, ErrorCode.NEED_LOGIN);
            }
        }else{
            sendError(response, ErrorCode.NEED_LOGIN);
        }
    }

    private void sendError(HttpServletResponse response, ErrorCode errorCode) throws IOException{
        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json,charset=utf-8");
        try(OutputStream os = response.getOutputStream()){
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, ErrorResponse.of(errorCode));
            os.flush();
        }
    }
    
}
