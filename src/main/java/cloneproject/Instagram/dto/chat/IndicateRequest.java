package cloneproject.Instagram.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IndicateRequest {

    private Long roomId;
    private Long senderId;
}
