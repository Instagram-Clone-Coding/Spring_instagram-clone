package cloneproject.Instagram.domain.dm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.dm.entity.MessageImage;

public interface MessageImageRepository extends JpaRepository<MessageImage, Long> {
}
