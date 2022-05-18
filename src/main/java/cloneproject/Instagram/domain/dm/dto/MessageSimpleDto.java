package cloneproject.Instagram.domain.dm.dto;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSimpleDto {

    private Long roomId;
    private Long messageId;
    private MemberDto member;

    public MessageSimpleDto(Message message, Member member) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.member = new MemberDto(member);
    }

}
