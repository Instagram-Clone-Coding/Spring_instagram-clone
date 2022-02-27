package cloneproject.Instagram.repository.chat;

import cloneproject.Instagram.entity.chat.JoinRoom;
import cloneproject.Instagram.entity.chat.Message;

import java.util.List;

public interface JoinRomRepositoryJdbc {

    void saveAllBatch(List<JoinRoom> joinRooms, Message message);

    void updateAllBatch(List<JoinRoom> updateJoinRooms, Message message);
}
