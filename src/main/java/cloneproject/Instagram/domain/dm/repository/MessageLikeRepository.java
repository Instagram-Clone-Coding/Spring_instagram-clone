package cloneproject.Instagram.domain.dm.repository;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.dm.entity.MessageLike;
import cloneproject.Instagram.domain.member.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {

    Optional<MessageLike> findByMemberAndMessage(Member member, Message message);
}
