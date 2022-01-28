package cloneproject.Instagram.dto.chat;

import cloneproject.Instagram.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomCreateResponse {

    private boolean status;
    private Long chatRoomId;
    private List<Opponent> opponents = new ArrayList<>();
}
