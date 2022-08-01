package cloneproject.Instagram.domain.dm.repository.jdbc;

import java.util.List;

import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.member.entity.Member;

public interface RoomMemberRepositoryJdbc {

	void saveAllBatch(Room room, List<Member> members);

}
