package cloneproject.Instagram.domain.member.entity.redis;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import cloneproject.Instagram.infra.geoip.dto.GeoIP;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash("refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken implements Serializable {

	@Id
	@Indexed
	private String id;
	// Long으로 하면 find 쿼리가 정상 작동하지 않음

	@Indexed
	private String value;

	@Indexed
	private Long memberId;

	@TimeToLive(unit = TimeUnit.DAYS)
	private Long timeout = 7L;

	private LocalDateTime lastUpdateDate;

	private String city;

	private String longitude;

	private String latitude;

	private String device;

	public void updateToken(String newValue) {
		this.lastUpdateDate = LocalDateTime.now();
		this.value = newValue;
	}

	public GeoIP getGeoIP() {
		return new GeoIP(city, longitude, latitude);
	}

	@Builder
	public RefreshToken(Long memberId, String value, GeoIP geoip, String device) {
		this.lastUpdateDate = LocalDateTime.now();
		this.memberId = memberId;
		this.value = value;
		this.city = geoip.getCity();
		this.longitude = geoip.getLongitude();
		this.latitude = geoip.getLatitude();
		this.device = device;
	}

}
