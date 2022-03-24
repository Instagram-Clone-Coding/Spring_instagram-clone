package cloneproject.Instagram.domain.dm.repository.jdbc;

import java.util.List;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.dm.entity.RoomUnreadMember;

public interface RoomUnreadMemberRepositoryJdbc {

    void saveAllBatch(List<RoomUnreadMember> roomUnreadMembers, Message message);
}
