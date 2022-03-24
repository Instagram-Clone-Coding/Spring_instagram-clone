package cloneproject.Instagram.domain.dm.repository.jdbc;

import java.util.List;

import cloneproject.Instagram.domain.dm.entity.JoinRoom;
import cloneproject.Instagram.domain.dm.entity.Message;

public interface JoinRomRepositoryJdbc {

    void saveAllBatch(List<JoinRoom> joinRooms, Message message);

    void updateAllBatch(List<JoinRoom> updateJoinRooms, Message message);
}
