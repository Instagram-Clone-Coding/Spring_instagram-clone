package cloneproject.Instagram.domain.dm.dto;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@Setter
@NoArgsConstructor
public class JoinRoomDto {

	private Long roomId;
	private MessageDto lastMessage;
	private boolean unreadFlag;
	private MemberSimpleInfo inviter;
	private List<MemberSimpleInfo> members = new ArrayList<>();

	@QueryProjection
	public JoinRoomDto(Long roomId, boolean unreadFlag, Member member) {
		this.roomId = roomId;
		this.unreadFlag = unreadFlag;
		this.inviter = new MemberSimpleInfo(member);
	}

}
