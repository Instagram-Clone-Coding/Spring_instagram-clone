package cloneproject.Instagram.domain.dm.dto;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
