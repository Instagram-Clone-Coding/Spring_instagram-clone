package cloneproject.Instagram.global.config.security;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

import static cloneproject.Instagram.global.error.ErrorCode.*;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                    AuthenticationException authException) throws IOException {
        response.setStatus(AUTHENTICATION_FAIL.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try(OutputStream os = response.getOutputStream()){
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, ErrorResponse.of(AUTHENTICATION_FAIL));
            os.flush();
        }
    }
    
}
