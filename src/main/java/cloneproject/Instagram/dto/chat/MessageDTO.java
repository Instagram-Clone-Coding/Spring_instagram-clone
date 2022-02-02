package cloneproject.Instagram.dto.chat;

import cloneproject.Instagram.entity.chat.Message;
import cloneproject.Instagram.entity.chat.MessageType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MessageDTO {

    private Long messageId;
    private MessageType messageType;
    private LocalDateTime messageDate;
    private Long senderId;
    private String content;

    @QueryProjection
    public MessageDTO(Message message) {
        this.messageId = message.getId();
        this.messageType = message.getType();
        this.content = message.getContent();
        this.senderId = message.getMember().getId();
        this.messageDate = message.getCreatedDate();
    }
}
