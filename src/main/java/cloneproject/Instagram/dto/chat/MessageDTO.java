package cloneproject.Instagram.dto.chat;

import cloneproject.Instagram.entity.chat.Message;
import cloneproject.Instagram.entity.chat.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageDTO {

    private Long messageId;
    private MessageType messageType;
    private String content;
    private Long userId;

    public MessageDTO(Message message) {
        this.messageId = message.getId();
        this.messageType = message.getType();
        this.content = message.getContent();
        this.userId = message.getMember().getId();
    }
}
