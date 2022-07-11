package cloneproject.Instagram.domain.dm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class MessageRequest {

    @NotNull(message = "채팅방 PK는 필수입니다.")
    private Long roomId;

    @NotNull(message = "메시지를 보내는 회원 PK는 필수입니다.")
    private Long senderId;

    @NotEmpty(message = "채팅 메시지는 필수입니다.")
    private String content;

}
