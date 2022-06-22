package cloneproject.Instagram.global.config.security;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.ErrorResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler{
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                    AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(AUTHORITY_INVALID.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try(OutputStream os = response.getOutputStream()){
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, ErrorResponse.of(AUTHORITY_INVALID));
            os.flush();
        }
    }

}
