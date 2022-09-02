package cloneproject.Instagram.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.member.entity.Block;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.global.config.QuerydslConfig;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@DataJpaTest
@Import(QuerydslConfig.class)
public class BlockRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Nested
    class ExistsByMemberIdAndBlockMemberId {

        @Test
        void blockingTarget_ReturnTrue() {
            // given
            final Member member = MemberUtils.newInstance();
            memberRepository.save(member);

            final Member target = MemberUtils.newInstance();
            memberRepository.save(target);

            final Block block = Block.builder()
                .member(member)
                .blockMember(target)
                .build();
            blockRepository.save(block);

            // when
            final boolean isPresent = blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), target.getId());

            //then
            assertThat(isPresent).isTrue();
        }

        @Test
        void notBlockingTarget_ReturnFalse() {
            // given
            final Member member = MemberUtils.newInstance();
            memberRepository.save(member);

            final Member target = MemberUtils.newInstance();
            memberRepository.save(target);

            // when
            final boolean isPresent = blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), target.getId());

            //then
            assertThat(isPresent).isFalse();
        }

    }

    @Nested
    class FindByMemberIdAndBlockMemberId {

        @Test
        void blockingTarget_ReturnBlock() {
            // given
            final Member member = MemberUtils.newInstance();
            memberRepository.save(member);

            final Member target = MemberUtils.newInstance();
            memberRepository.save(target);

            final Block block = Block.builder()
                .member(member)
                .blockMember(target)
                .build();
            blockRepository.save(block);

            // when
            final boolean isPresent = blockRepository.findByMemberIdAndBlockMemberId(member.getId(), target.getId())
                .isPresent();

            //then
            assertThat(isPresent).isTrue();

        }

        @Test
        void notBlockingTarget_ReturnEmpty() {
            // given
            final Member member = MemberUtils.newInstance();
            memberRepository.save(member);

            final Member target = MemberUtils.newInstance();
            memberRepository.save(target);

            // when
            final boolean isEmpty = blockRepository.findByMemberIdAndBlockMemberId(member.getId(), target.getId())
                .isEmpty();

            //then
            assertThat(isEmpty).isTrue();

        }

    }

}
