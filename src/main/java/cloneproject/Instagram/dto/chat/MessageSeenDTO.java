package cloneproject.Instagram.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSeenDTO {

    private Long roomId;
    private Long memberId;
    private LocalDateTime seenDate;
}
