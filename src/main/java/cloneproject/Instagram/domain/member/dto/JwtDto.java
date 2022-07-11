package cloneproject.Instagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtDto {

	private String type;

	private String accessToken;

	private String refreshToken;

}
