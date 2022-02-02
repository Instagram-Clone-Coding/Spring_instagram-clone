package cloneproject.Instagram.dto.chat;

import cloneproject.Instagram.entity.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JoinRoomDTO {

    private Long chatRoomId;
    private MessageDTO lastMessage;
    private boolean unreadFlag;
    private MemberSimpleInfo inviter;
    private List<MemberSimpleInfo> invitees = new ArrayList<>();

    @QueryProjection
    public JoinRoomDTO(Long chatRoomId, boolean unreadFlag, Member member) {
        this.chatRoomId = chatRoomId;
        this.unreadFlag = unreadFlag;
        this.inviter = new MemberSimpleInfo(member);
    }
}
