package cloneproject.Instagram.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomInquireResponse {

    private boolean status;
    private Long unseenCount;
}
