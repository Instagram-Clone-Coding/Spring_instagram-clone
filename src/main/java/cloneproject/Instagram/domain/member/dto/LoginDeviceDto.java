package cloneproject.Instagram.domain.member.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import cloneproject.Instagram.infra.location.dto.Location;

@Getter
@Builder
@AllArgsConstructor
public class LoginDeviceDto {

	private String tokenId;
	private Location location;
	private String device;
	private LocalDateTime lastLoginDate;
	private boolean isCurrent;

}
