package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.member.LikeMembersDTO;
import cloneproject.Instagram.dto.member.QLikeMembersDTO;
import cloneproject.Instagram.repository.story.MemberStoryRedisRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static cloneproject.Instagram.entity.comment.QCommentLike.commentLike;
import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.member.QMember.member;
import static cloneproject.Instagram.entity.post.QPostLike.postLike;

@RequiredArgsConstructor
public class PostLikeRepositoryQuerydslImpl implements PostLikeRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;
    private final MemberStoryRedisRepository memberStoryRedisRepository;

    @Override
    public Page<LikeMembersDTO> findLikeMembersDtoPageByPostIdAndMemberId(Pageable pageable, Long postId, Long memberId) {
        final List<LikeMembersDTO> likeMembersDTOs = queryFactory
                .select(new QLikeMembersDTO(
                        postLike.member,
                        JPAExpressions
                                .selectFrom(follow)
                                .where(
                                        follow.member.id.eq(memberId)
                                                .and(follow.followMember.eq(postLike.member))
                                )
                                .exists(),
                        JPAExpressions
                                .selectFrom(follow)
                                .where(
                                        follow.member.eq(postLike.member)
                                                .and(follow.followMember.id.eq(memberId))
                                )
                                .exists()
                ))
                .from(postLike)
                .innerJoin(postLike.member, member)
                .where(
                        postLike.post.id.eq(postId)
                                .and(postLike.member.id.ne(memberId))
                )
                .orderBy(postLike.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        likeMembersDTOs.forEach(dto -> dto.setHasStory(memberStoryRedisRepository.findAllByMemberId(dto.getMember().getId()).size() > 0));

        final long total = queryFactory
                .selectFrom(postLike)
                .where(
                        postLike.post.id.eq(postId)
                                .and(postLike.member.id.ne(memberId))
                )
                .fetchCount();

        return new PageImpl<>(likeMembersDTOs, pageable, total);
    }

    @Override
    public Page<LikeMembersDTO> findLikeMembersDtoPageByCommentIdAndMemberId(Pageable pageable, Long commentId, Long memberId) {
        final List<LikeMembersDTO> likeMembersDTOs = queryFactory
                .select(new QLikeMembersDTO(
                        commentLike.member,
                        JPAExpressions
                                .selectFrom(follow)
                                .where(
                                        follow.member.id.eq(memberId)
                                                .and(follow.followMember.eq(commentLike.member))
                                )
                                .exists(),
                        JPAExpressions
                                .selectFrom(follow)
                                .where(
                                        follow.member.eq(commentLike.member)
                                                .and(follow.followMember.id.eq(memberId))
                                )
                                .exists()
                ))
                .from(commentLike)
                .innerJoin(commentLike.member, member)
                .where(
                        commentLike.comment.id.eq(commentId)
                                .and(commentLike.member.id.ne(memberId))
                )
                .orderBy(commentLike.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        likeMembersDTOs.forEach(dto -> dto.setHasStory(memberStoryRedisRepository.findAllByMemberId(dto.getMember().getId()).size() > 0));

        final long total = queryFactory
                .selectFrom(commentLike)
                .where(
                        commentLike.comment.id.eq(commentId)
                                .and(commentLike.member.id.ne(memberId))
                )
                .fetchCount();

        return new PageImpl<>(likeMembersDTOs, pageable, total);
    }
}
