package cloneproject.Instagram.domain.member.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.member.dto.LoginDevicesDto;
import cloneproject.Instagram.domain.member.entity.redis.RefreshToken;
import cloneproject.Instagram.domain.member.exception.JwtInvalidException;
import cloneproject.Instagram.domain.member.repository.redis.RefreshTokenRedisRepository;
import cloneproject.Instagram.infra.location.dto.Location;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRedisRepository refreshTokenRedisRepository;
	@Value("${max-login-device}")
	private long MAX_LOGIN_DEVICE;
	@Value("${refresh-token-expires}")
	private long REFRESH_TOKEN_EXPIRES;

	@Transactional
	public void addRefreshToken(Long memberId, String tokenValue, String device, Location location) {
		final List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(memberId)
			.stream()
			.sorted(Comparator.comparing(RefreshToken::getLastUpdateDate))
			.collect(Collectors.toList());

		for (int i = 0; i <= refreshTokens.size() - MAX_LOGIN_DEVICE; ++i) {
			refreshTokenRedisRepository.deleteById(refreshTokens.get(i).getId());
			refreshTokens.remove(i);
		}

		final RefreshToken refreshToken = RefreshToken.builder()
			.memberId(memberId)
			.value(tokenValue)
			.device(device)
			.location(location)
			.build();

		refreshTokenRedisRepository.save(refreshToken);
	}

	@Transactional
	public Optional<RefreshToken> findRefreshToken(Long memberId, String value) {
		return refreshTokenRedisRepository.findByMemberIdAndValue(memberId, value);
	}

	@Transactional
	public void updateRefreshToken(RefreshToken refreshToken, String newToken) {
		final RefreshToken newRefreshToken = RefreshToken.builder()
			.memberId(refreshToken.getMemberId())
			.value(newToken)
			.device(refreshToken.getDevice())
			.location(refreshToken.getLocation())
			.build();
		refreshTokenRedisRepository.delete(refreshToken);
		refreshTokenRedisRepository.save(newRefreshToken);
	}

	@Transactional
	public void deleteRefreshTokenWithValue(Long memberId, String value) {
		final RefreshToken refreshToken = refreshTokenRedisRepository.findByMemberIdAndValue(memberId, value)
			.orElseThrow(JwtInvalidException::new);
		refreshTokenRedisRepository.delete(refreshToken);
	}

	@Transactional
	public void deleteRefreshToken(RefreshToken refreshToken) {
		refreshTokenRedisRepository.delete(refreshToken);
	}

	@Transactional
	public void deleteRefreshTokenWithId(Long memberId, String id) {
		final RefreshToken refreshToken = refreshTokenRedisRepository.findByMemberIdAndId(memberId, id)
			.orElseThrow(JwtInvalidException::new);
		refreshTokenRedisRepository.delete(refreshToken);
	}

	@Transactional(readOnly = true)
	public List<LoginDevicesDto> getLoginDevices(Long memberId, String currentToken) {
		final List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(memberId)
			.stream()
			.sorted(Comparator.comparing(RefreshToken::getLastUpdateDate).reversed())
			.collect(Collectors.toList());
		return refreshTokens.stream()
			.map(rt -> convertRefreshTokenToLoginedDevicesDTO(rt, currentToken))
			.collect(Collectors.toList());
	}

	private LoginDevicesDto convertRefreshTokenToLoginedDevicesDTO(RefreshToken refreshToken, String currentToken) {
		return LoginDevicesDto.builder()
			.tokenId(refreshToken.getId())
			.device(refreshToken.getDevice())
			.location(refreshToken.getLocation())
			.lastLoginDate(refreshToken.getLastUpdateDate())
			.isCurrent(currentToken.equals(refreshToken.getValue()))
			.build();
	}

}
