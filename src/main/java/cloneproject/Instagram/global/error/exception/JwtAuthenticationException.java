package cloneproject.Instagram.global.error.exception;

import org.springframework.security.core.AuthenticationException;

import cloneproject.Instagram.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

	public JwtAuthenticationException(){
		super(ErrorCode.JWT_INVALID.getMessage());
	}

}
