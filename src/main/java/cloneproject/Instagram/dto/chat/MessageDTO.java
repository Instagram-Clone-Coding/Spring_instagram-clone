package cloneproject.Instagram.dto.chat;

import cloneproject.Instagram.entity.chat.Message;
import cloneproject.Instagram.entity.chat.MessageType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long roomId;
    private Long messageId;
    private Long senderId;
    private String content;
    private MessageType messageType;
    private LocalDateTime messageDate;

    @QueryProjection
    public MessageDTO(Message message) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.messageType = message.getType();
        this.content = message.getContent();
        this.senderId = message.getMember().getId();
        this.messageDate = message.getCreatedDate();
    }
}
