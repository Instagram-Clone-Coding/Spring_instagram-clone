package cloneproject.Instagram.domain.member.dto;

import java.time.LocalDateTime;

import cloneproject.Instagram.infra.geoip.dto.GeoIP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginDevicesDto {

	private String tokenId;
	private GeoIP location;
	private String device;
	private LocalDateTime lastLoginDate;

}
