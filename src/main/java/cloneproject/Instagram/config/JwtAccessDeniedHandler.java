package cloneproject.Instagram.config;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.dto.error.ErrorCode;
import cloneproject.Instagram.dto.error.ErrorResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler{
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                    AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 필요한 권한이 없이 접근하려 할때 403
        sendError(response, ErrorCode.NO_AUTHORITY);
        // response.sendError(HttpServletResponse.SC_FORBIDDEN);
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
