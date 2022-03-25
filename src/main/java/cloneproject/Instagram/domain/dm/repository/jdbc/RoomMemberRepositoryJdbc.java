package cloneproject.Instagram.domain.dm.repository.jdbc;

import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.member.entity.Member;

import java.util.List;

public interface RoomMemberRepositoryJdbc {

    void saveAllBatch(Room room, List<Member> members);
}
