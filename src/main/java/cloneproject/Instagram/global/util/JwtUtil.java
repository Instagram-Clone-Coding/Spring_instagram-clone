package cloneproject.Instagram.global.util;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import cloneproject.Instagram.domain.member.dto.JwtDto;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.JwtExpiredException;
import cloneproject.Instagram.domain.member.exception.JwtInvalidException;
import cloneproject.Instagram.global.config.security.token.JwtAuthenticationToken;
import cloneproject.Instagram.global.error.exception.BusinessException;

@Component
public class JwtUtil {

	private final static String CLAIM_AUTHORITIES_KEY = "authorities";
	private final static String CLAIM_JWT_TYPE_KEY = "type";
	private final static String BEARER_TYPE_PREFIX = "Bearer ";
	private final static String BEARER_TYPE = "Bearer";
	private static final int JWT_PREFIX_LENGTH = 7;
	private final Key JWT_KEY;
	@Value("${access-token-expires}")
	private long ACCESS_TOKEN_EXPIRES;
	@Value("${refresh-token-expires}")
	private long REFRESH_TOKEN_EXPIRES;

	public JwtUtil(@Value("${jwt.key}") byte[] key) {
		this.JWT_KEY = Keys.hmacShaKeyFor(key);
	}

	public String extractJwt(String authenticationHeader) {
		if (authenticationHeader == null) {
			throw new JwtInvalidException();
		} else if (!authenticationHeader.startsWith(BEARER_TYPE_PREFIX)) {
			throw new JwtInvalidException();
		}
		return authenticationHeader.substring(JWT_PREFIX_LENGTH);
	}

	public Authentication getAuthentication(String token) throws BusinessException {
		Claims claims = parseClaims(token);
		final List<SimpleGrantedAuthority> authorities = Arrays.stream(
				claims.get(CLAIM_AUTHORITIES_KEY).toString().split(","))
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
		final User principal = new User(claims.getSubject(), "", authorities);

		return JwtAuthenticationToken.of(principal, token, authorities);
	}

	public JwtDto generateJwtDto(Authentication authentication) {
		final String authoritiesString = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
		final long currentTime = (new Date()).getTime();

		final Date accessTokenExpiresIn = new Date(currentTime + ACCESS_TOKEN_EXPIRES);
		final Date refreshTokenExpiresIn = new Date(currentTime + REFRESH_TOKEN_EXPIRES);

		final String accessToken = Jwts.builder()
			.setSubject(authentication.getName())
			.claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
			.claim(CLAIM_JWT_TYPE_KEY, BEARER_TYPE)
			.setExpiration(accessTokenExpiresIn)
			.signWith(JWT_KEY, SignatureAlgorithm.HS512)
			.compact();

		final String refreshToken = Jwts.builder()
			.setSubject(authentication.getName())
			.claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
			.setExpiration(refreshTokenExpiresIn)
			.signWith(JWT_KEY, SignatureAlgorithm.HS512)
			.compact();

		return JwtDto.builder()
			.type(BEARER_TYPE)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public JwtDto generateJwtDto(Member member) {
		final String authoritiesString = member.getRole().toString();
		long currentTime = (new Date()).getTime();

		final Date accessTokenExpiresIn = new Date(currentTime + ACCESS_TOKEN_EXPIRES);
		final Date refreshTokenExpiresIn = new Date(currentTime + REFRESH_TOKEN_EXPIRES);

		final String accessToken = Jwts.builder()
			.setSubject(member.getId().toString())
			.claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
			.claim(CLAIM_JWT_TYPE_KEY, BEARER_TYPE)
			.setExpiration(accessTokenExpiresIn)
			.signWith(JWT_KEY, SignatureAlgorithm.HS512)
			.compact();

		final String refreshToken = Jwts.builder()
			.setSubject(member.getId().toString())
			.claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
			.setExpiration(refreshTokenExpiresIn)
			.signWith(JWT_KEY, SignatureAlgorithm.HS512)
			.compact();

		return JwtDto.builder()
			.type(BEARER_TYPE)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}


	private Claims parseClaims(String token) throws BusinessException {
		try {
			return Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			throw new JwtExpiredException();
		} catch (Exception e) {
			throw new JwtInvalidException();
		}
	}

}
