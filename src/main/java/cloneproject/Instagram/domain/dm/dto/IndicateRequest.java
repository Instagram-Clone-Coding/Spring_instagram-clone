package cloneproject.Instagram.domain.dm.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndicateRequest {

	private Long roomId;
	private Long senderId;

}
