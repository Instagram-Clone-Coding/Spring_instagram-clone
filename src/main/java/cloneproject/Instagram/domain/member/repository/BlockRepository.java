package cloneproject.Instagram.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.member.entity.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {

	public boolean existsByMemberIdAndBlockMemberId(Long memberId, Long blockMemberId);

	public Optional<Block> findByMemberIdAndBlockMemberId(Long memberId, Long blockMemberId);

}
