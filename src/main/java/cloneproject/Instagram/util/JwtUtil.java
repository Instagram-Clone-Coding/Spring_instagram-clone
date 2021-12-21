package cloneproject.Instagram.util;


import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import cloneproject.Instagram.dto.member.JwtDto;
import cloneproject.Instagram.exception.ExpiredAccessTokenException;
import cloneproject.Instagram.exception.ExpiredRefreshTokenException;
import cloneproject.Instagram.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    
    private final static long ACCESS_TOKEN_EXPIRES = 1000 * 60 * 10; // 10분
    private final static long REFRESH_TOKEN_EXPIRES = 1000 * 60 * 60 * 24 * 7; // 7일
    
    private final static String BEARER_TYPE = "Bearer";
    private final static String AUTHENTITIES_KEY = "auth";
    
    private final Key accessKey;
    private final Key refreshKey;


    public JwtUtil(@Value("${jwt.access.secret}") byte[] accessSecret, 
                    @Value("${jwt.refresh.secret}") byte[] refreshSecret){
        // byte[] keyBytes = Decoders.BASE64.decode(accessSecret);
        this.accessKey = Keys.hmacShaKeyFor(accessSecret);
        // keyBytes = Decoders.BASE64.decode(refreshSecret);
        refreshKey = Keys.hmacShaKeyFor(refreshSecret);
    }

    public JwtDto generateTokenDto(Authentication authentication){
        // getAuthorities를 그대로 token에 넣으면 객체가 깔끔하지 않다
        // 따라서 변환 과정을 거침
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
            
        // Token 생성
        long currentTime = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(currentTime + ACCESS_TOKEN_EXPIRES);
        Date refreshTokenExpiresIn = new Date(currentTime + REFRESH_TOKEN_EXPIRES);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       
                .claim(AUTHENTITIES_KEY, authorities.get(0))
                .setExpiration(accessTokenExpiresIn)    
                .signWith(accessKey, SignatureAlgorithm.HS512)  
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();

        return JwtDto.builder()
                .type(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpires(accessTokenExpiresIn)
                .build();
    }
    
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        
        if (claims.get(AUTHENTITIES_KEY) == null) {
            throw new InvalidJwtException();
        }

        String authorityString = claims.get(AUTHENTITIES_KEY).toString();

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(authorityString));

        // UserDetails 객체를 만들어서 Authentication 리턴
        // UsernamePasswordAuthenticationToken의 principal에 username(string)만 넣어놓아도 실행은 똑같이 된다.
        // 하지만 나중에 여러 정보를 포함 할 경우를 대비하기 위해 User 객체를 만들어서 사용
        // 참고: https://codevang.tistory.com/273
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateAccessJwt(String token){
        return validateJwt(token, accessKey);
    }

    public boolean validateRefeshJwt(String token){
        return validateJwt(token, refreshKey);
    }

    private boolean validateJwt(String token, Key key){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch(ExpiredJwtException e){
            if(key == refreshKey){
                throw new ExpiredRefreshTokenException();
            }else{
                throw new ExpiredAccessTokenException();
            }
        } catch (JwtException e) {
            throw new InvalidJwtException();
        }
    }

    private Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken).getBody();
    }

}
