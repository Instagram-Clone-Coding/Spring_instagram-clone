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

import cloneproject.Instagram.domain.member.dto.LoginDeviceDto;
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

	@Transactional
	public void addRefreshToken(Long memberId, String tokenValue, String device, Location location) {
		deleteExceedRefreshTokens(memberId);

		final RefreshToken refreshToken = RefreshToken.builder()
			.memberId(memberId)
			.value(tokenValue)
			.device(device)
			.location(location)
			.build();
		refreshTokenRedisRepository.save(refreshToken);
	}

	@Transactional(readOnly = true)
	public Optional<RefreshToken> findRefreshToken(Long memberId, String value) {
		return refreshTokenRedisRepository.findByMemberIdAndValue(memberId, value);
	}

	@Transactional
	public void deleteRefreshToken(RefreshToken refreshToken) {
		refreshTokenRedisRepository.delete(refreshToken);
	}

	@Transactional
	public void deleteRefreshTokenByValue(Long memberId, String value) {
		final RefreshToken refreshToken = refreshTokenRedisRepository.findByMemberIdAndValue(memberId, value)
			.orElseThrow(JwtInvalidException::new);
		refreshTokenRedisRepository.delete(refreshToken);
	}

	@Transactional
	public void deleteRefreshTokenByMemberIdAndId(Long memberId, String id) {
		final RefreshToken refreshToken = refreshTokenRedisRepository.findByMemberIdAndId(memberId, id)
			.orElseThrow(JwtInvalidException::new);
		refreshTokenRedisRepository.delete(refreshToken);
	}

	@Transactional(readOnly = true)
	public List<LoginDeviceDto> getLoginDevices(Long memberId, String currentToken) {
		final List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(memberId)
			.stream()
			.sorted(Comparator.comparing(RefreshToken::getLastUpdateDate).reversed())
			.collect(Collectors.toList());
		return refreshTokens.stream()
			.map(rt -> convertRefreshTokenToLoginDevicesDto(rt, currentToken))
			.collect(Collectors.toList());
	}

	private LoginDeviceDto convertRefreshTokenToLoginDevicesDto(RefreshToken refreshToken, String currentToken) {
		return LoginDeviceDto.builder()
			.tokenId(refreshToken.getId())
			.device(refreshToken.getDevice())
			.location(refreshToken.getLocation())
			.lastLoginDate(refreshToken.getLastUpdateDate())
			.isCurrent(currentToken.equals(refreshToken.getValue()))
			.build();
	}

	private void deleteExceedRefreshTokens(Long memberId){
		final List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(memberId)
			.stream()
			.sorted(Comparator.comparing(RefreshToken::getLastUpdateDate))
			.collect(Collectors.toList());

		for (int i = 0; i <= refreshTokens.size() - MAX_LOGIN_DEVICE; ++i) {
			refreshTokenRedisRepository.deleteById(refreshTokens.get(i).getId());
			refreshTokens.remove(i);
		}
	}

}
