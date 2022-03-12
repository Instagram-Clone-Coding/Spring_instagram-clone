package cloneproject.Instagram.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.member.RefreshToken;
import cloneproject.Instagram.exception.InvalidJwtException;
import cloneproject.Instagram.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    @Value("${max-login-device}")
    private long MAX_LOGIN_DEVICE;

    @Value("${refresh-token-expires}")
    private long REFRESH_TOKEN_EXPIRES;
    
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void addRefreshToken(Member member, String tokenValue, String device, String location){
        deleteExpiredRefreshToken();

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllWithMemberIdAndDateAfter(
            member.getId(), getLocalDateTimeBoundary()
        );

        for(int i = 0; i <= refreshTokens.size()-MAX_LOGIN_DEVICE; ++i){
            refreshTokens.get(i).deleteToken();
        }
        RefreshToken refreshToken = RefreshToken.builder()
            .member(member)
            .value(tokenValue)
            .device(device)
            .location(location)
            .build();

        refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional(readOnly = true)
    public RefreshToken findRefreshToken(Long memberId, String tokenValue){
        RefreshToken result = refreshTokenRepository.findWithMemberIdAndDateAfterAndValue(
            memberId, getLocalDateTimeBoundary(), tokenValue
        ).orElseThrow(InvalidJwtException::new);
        return result;
    }

    @Transactional
    public void updateRefreshToken(RefreshToken refreshToken, String newToken){
        refreshToken.updateToken(newToken);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String value){
        refreshTokenRepository.deleteByValue(value);
    }

    private LocalDateTime getLocalDateTimeBoundary(){
        LocalDateTime time = LocalDateTime.now();
        return time.minus(REFRESH_TOKEN_EXPIRES, ChronoUnit.MILLIS);
    }

    @Transactional
    private void deleteExpiredRefreshToken(){
        refreshTokenRepository.deleteAllByLastModifiedAtBefore(getLocalDateTimeBoundary());
    }
    
}
