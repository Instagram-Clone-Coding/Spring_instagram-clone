package cloneproject.Instagram.global.config.security.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloneproject.Instagram.global.error.ErrorResponse;
import cloneproject.Instagram.global.error.exception.BusinessException;

@Component
public class CustomExceptionHandleFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (BusinessException e) {
			final ErrorResponse errorCode = ErrorResponse.of(e.getErrorCode(), e.getErrors());
			response.setStatus(errorCode.getStatus());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			try (OutputStream os = response.getOutputStream()) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.writeValue(os, errorCode);
				os.flush();
			}
		}
	}

}
