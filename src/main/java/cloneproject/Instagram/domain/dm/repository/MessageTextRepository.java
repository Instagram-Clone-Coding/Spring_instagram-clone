package cloneproject.Instagram.domain.dm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.dm.entity.MessageText;

public interface MessageTextRepository extends JpaRepository<MessageText, Long> {
}
