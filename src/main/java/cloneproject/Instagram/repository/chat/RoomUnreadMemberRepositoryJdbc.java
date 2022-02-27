package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.RoomUnreadMember;

import java.util.List;

public interface RoomUnreadMemberRepositoryJdbc {

    void saveAllBatch(List<RoomUnreadMember> roomUnreadMembers);
}
