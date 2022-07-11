package cloneproject.Instagram.domain.dm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.MessageText;

public interface MessageTextRepository extends JpaRepository<MessageText, Long> {

	@Query("select mt from MessageText mt where mt.id in :messageIds")
	List<MessageText> findAllByIdIn(@Param("messageIds") List<Long> messageIds);

}
