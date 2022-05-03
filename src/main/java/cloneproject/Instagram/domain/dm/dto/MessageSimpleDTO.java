package cloneproject.Instagram.domain.dm.dto;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSimpleDTO {

    private Long roomId;
    private Long messageId;
    private MemberDTO member;

    public MessageSimpleDTO(Message message, Member member) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.member = new MemberDTO(member);
    }
}
