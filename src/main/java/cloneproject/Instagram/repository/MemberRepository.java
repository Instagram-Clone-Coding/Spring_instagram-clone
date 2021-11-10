package cloneproject.Instagram.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
    public Optional<Member> findByUsername(String username);
    public boolean existsByUsername(String username);
}
