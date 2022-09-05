package cloneproject.Instagram.util.domain.member.redis;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.member.entity.redis.RefreshToken;
import cloneproject.Instagram.infra.location.dto.Location;
import cloneproject.Instagram.util.LocationUtils;

public class RefreshTokenUtils {

	public static RefreshToken newInstance(Long memberId) {
		final String value = RandomStringUtils.random(20, true, true);
		final Location location = LocationUtils.newInstance();
		final String device = RandomStringUtils.random(20, true, true);
		return of(memberId, value, location, device);
	}

	public static RefreshToken of(Long memberId, String value, Location location, String device) {
		return RefreshToken.builder()
			.memberId(memberId)
			.value(value)
			.location(location)
			.device(device)
			.build();
	}

}
