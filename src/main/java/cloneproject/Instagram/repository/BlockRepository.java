package cloneproject.Instagram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.member.Block;

public interface BlockRepository extends JpaRepository<Block, Long>{

    public boolean existsByMemberIdAndBlockMemberId(Long memberId, Long blockMemberId);
    public Optional<Block> findByMemberIdAndBlockMemberId(Long memberId, Long blockMemberId);
    public List<Block> findAllByMemberId(Long memberId);
    public List<Block> findAllByBlockMemberId(Long blockMemberId);

}
