package cloneproject.Instagram.dto.chat;

import cloneproject.Instagram.entity.chat.Message;
import cloneproject.Instagram.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSimpleDTO {

    private Long roomId;
    private Long messageId;
    private Long memberId;

    public MessageSimpleDTO(Message message, Member member) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.memberId = member.getId();
    }
}
