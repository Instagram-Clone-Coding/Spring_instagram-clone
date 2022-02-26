package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.JoinRoom;

import java.util.List;

public interface JoinRomRepositoryJdbc {

    void saveAllBatch(List<JoinRoom> joinRooms);

    void updateAllBatch(List<JoinRoom> updateJoinRooms);
}
