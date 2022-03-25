package cloneproject.Instagram.domain.dm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.dm.entity.MessagePost;

public interface MessagePostRepository extends JpaRepository<MessagePost, Long> {
}
