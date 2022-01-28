package cloneproject.Instagram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cloneproject.Instagram.entity.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member>, MemberPostRepositoryQuerydsl,MemberRepositoryQuerydsl{
    public Optional<Member> findByUsername(String username);
    public Optional<Member> findById(Long id);
    public List<Member> findAll(Specification<Member> spec);
    public boolean existsByUsername(String username);
}
