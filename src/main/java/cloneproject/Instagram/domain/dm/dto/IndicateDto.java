package cloneproject.Instagram.domain.dm.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IndicateDto {

	private Long roomId;
	private Long senderId;
	private Long ttl;

}
