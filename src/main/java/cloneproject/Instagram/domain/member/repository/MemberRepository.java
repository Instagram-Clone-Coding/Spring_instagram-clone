package cloneproject.Instagram.domain.member.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.querydsl.MemberPostRepositoryQuerydsl;
import cloneproject.Instagram.domain.member.repository.querydsl.MemberRepositoryQuerydsl;

public interface MemberRepository
	extends JpaRepository<Member, Long>, MemberPostRepositoryQuerydsl, MemberRepositoryQuerydsl {

	Optional<Member> findByUsername(String username);

	boolean existsByUsername(String username);

	List<Member> findAllByUsernameIn(Collection<String> usernames);

	List<Member> findAllByIdIn(Collection<Long> ids);

}
