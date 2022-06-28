package cloneproject.Instagram.global.config.security.provider;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import cloneproject.Instagram.domain.member.exception.PasswordResetFailException;
import cloneproject.Instagram.domain.member.service.EmailCodeService;
import cloneproject.Instagram.global.config.security.token.ResetPasswordCodeAuthenticationToken;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResetPasswordCodeAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
	private final UserDetailsService userDetailsService;

	private final EmailCodeService emailCodeService;

	protected void additionalAuthenticationChecks(
		UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			this.logger.debug("Failed to authenticate since no credentials provided");
			throw new PasswordResetFailException();
		} else {
			String presentedEmailCode = authentication.getCredentials().toString();
			if (!presentedEmailCode.equals(userDetails.getPassword())) {
				this.logger.debug("Failed to authenticate since email code does not match stored value");
				throw new PasswordResetFailException();
			}
		}
	}

	protected void doAfterPropertiesSet() {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws
		AuthenticationException {
		try {
			UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
			if (loadedUser == null) {
				throw new EntityNotFoundException(MEMBER_NOT_FOUND);
			} else {
				return loadedUser;
			}
		} catch (InternalAuthenticationServiceException e) {
			throw e;
		}
	}

	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
		UserDetails user) {
		this.deleteResetPasswordCode(authentication);
		return super.createSuccessAuthentication(principal, authentication, user);
	}

	protected UserDetailsService getUserDetailsService() {
		return this.userDetailsService;
	}

	protected void deleteResetPasswordCode(Authentication authentication) {
		this.emailCodeService.deleteResetPasswordCode(determineUsername(authentication));
	}

	private String determineUsername(Authentication authentication) {
		return authentication.getName();
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return ResetPasswordCodeAuthenticationToken.class.isAssignableFrom(aClass);
	}

}