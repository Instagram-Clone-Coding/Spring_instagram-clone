package cloneproject.Instagram.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRoomDTO {

    private Long chatRoomId;
    private MessageDTO lastMessage;
    private Long unseenCount;
    private List<Opponent> opponents = new ArrayList<>();
}
