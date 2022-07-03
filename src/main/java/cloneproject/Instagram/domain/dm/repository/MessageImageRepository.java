package cloneproject.Instagram.domain.dm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.MessageImage;

public interface MessageImageRepository extends JpaRepository<MessageImage, Long> {

	@Query("select mi from MessageImage mi where mi.id in :messageIds")
	List<MessageImage> findAllByIdIn(@Param("messageIds") List<Long> messageIds);

}
