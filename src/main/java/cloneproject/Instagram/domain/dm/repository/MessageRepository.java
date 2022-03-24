package cloneproject.Instagram.domain.dm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.dm.repository.querydsl.MessageRepositoryQuerydsl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryQuerydsl {

    @Query("select m from Message m join fetch m.room where m.id = :id")
    Optional<Message> findWithRoomById(@Param("id") Long id);

    Page<Message> findAllByRoom(Room room, Pageable pageable);

    Long countByCreatedDateBetweenAndRoom(LocalDateTime start, LocalDateTime end, Room room);

    List<Message> findTop2ByCreatedDateBetweenAndRoomOrderByIdDesc(LocalDateTime start, LocalDateTime end, Room room);
}
