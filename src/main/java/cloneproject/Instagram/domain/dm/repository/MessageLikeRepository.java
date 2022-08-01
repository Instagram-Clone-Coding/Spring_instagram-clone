package cloneproject.Instagram.domain.dm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.dm.entity.MessageLike;
import cloneproject.Instagram.domain.member.entity.Member;

public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {

	Optional<MessageLike> findByMemberAndMessage(Member member, Message message);

	@Query("select ml from MessageLike ml join fetch ml.member where ml.message.id in :messageIds")
	List<MessageLike> findAllWithMemberByMessageIdIn(@Param("messageIds") List<Long> messageIds);

}
