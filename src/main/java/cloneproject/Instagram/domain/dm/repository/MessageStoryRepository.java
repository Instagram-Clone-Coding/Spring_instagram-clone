package cloneproject.Instagram.domain.dm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.MessageStory;

public interface MessageStoryRepository extends JpaRepository<MessageStory, Long> {

	@Query("select ms from MessageStory ms join fetch ms.story where ms.id in :messageIds")
	List<MessageStory> findAllWithStoryByIdIn(@Param("messageIds") List<Long> messageIds);

}
