package cloneproject.Instagram.domain.dm.dto;

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
    private MemberSimpleInfo inviter;
    private List<MemberSimpleInfo> members = new ArrayList<>();
}
