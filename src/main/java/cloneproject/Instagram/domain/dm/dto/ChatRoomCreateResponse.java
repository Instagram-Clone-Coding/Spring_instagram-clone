package cloneproject.Instagram.domain.dm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateResponse {

	private boolean status;
	private Long chatRoomId;
	private MemberSimpleInfo inviter;
	private List<MemberSimpleInfo> members = new ArrayList<>();

}
