package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.Room;
import cloneproject.Instagram.entity.member.Member;

import java.util.List;

public interface RoomMemberRepositoryJdbc {

    void saveAllBatch(Room room, List<Member> members);
}
