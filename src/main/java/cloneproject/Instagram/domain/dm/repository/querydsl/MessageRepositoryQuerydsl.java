package cloneproject.Instagram.domain.dm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.dm.entity.JoinRoom;
import cloneproject.Instagram.domain.dm.entity.Message;

public interface MessageRepositoryQuerydsl {

    Page<Message> findAllByJoinRoom(JoinRoom joinRoom, Pageable pageable);

}
