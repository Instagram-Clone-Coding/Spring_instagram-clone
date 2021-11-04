package cloneproject.Instagram.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
    public Optional<Member> findByUserid(String userid);
}
