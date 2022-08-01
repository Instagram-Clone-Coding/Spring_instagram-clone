package cloneproject.Instagram.domain.dm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSeenDto {

	private Long roomId;
	private Long memberId;
	private LocalDateTime seenDate;

}
