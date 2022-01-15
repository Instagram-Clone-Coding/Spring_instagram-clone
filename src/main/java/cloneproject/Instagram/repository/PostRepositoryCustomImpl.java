package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.post.*;
import cloneproject.Instagram.entity.comment.QComment;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.member.QMember;
import cloneproject.Instagram.entity.post.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.post.QBookmark.bookmark;
import static cloneproject.Instagram.entity.post.QPost.post;
import static cloneproject.Instagram.entity.post.QPostImage.postImage;
import static cloneproject.Instagram.entity.post.QPostLike.postLike;
import static cloneproject.Instagram.entity.post.QPostTag.postTag;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Slf4j
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostDTO> findPostDtoPage(Member member, Pageable pageable) {
        if (member.getFollowings().isEmpty())
            return null;

        // TODO: 최신 댓글 2개 추가 -> 댓글 API 구현할 때 업데이트
        final List<PostDTO> postDTOs = queryFactory
                .select(new QPostDTO(
                        post.id,
                        post.content,
                        post.uploadDate,
                        post.member.username,
                        post.member.name,
                        post.member.image.imageUrl,
                        post.comments.size(),
                        post.postLikes.size(),
                        JPAExpressions
                                .selectFrom(bookmark)
                                .where(bookmark.post.eq(post).and(bookmark.member.eq(member)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(postLike)
                                .where(postLike.post.eq(post).and(postLike.member.eq(member)))
                                .exists(),
                        JPAExpressions
                                .select(postLike.member.username)
                                .from(postLike)
                                .where(postLike.post.eq(post)
                                        .and(postLike.member
                                                .in(JPAExpressions
                                                        .select(follow.followMember)
                                                        .from(follow)
                                                        .where(follow.member.eq(member)))))
                                .limit(1)
                ))
                .from(post)
                .join(QMember.member)
                .on(post.member.username.in(
                        JPAExpressions
                                .select(follow.followMember.username)
                                .from(follow)
                                .where(follow.member.eq(member))
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetch();

        final List<Long> postIds = postDTOs.stream()
                .map(PostDTO::getPostId)
                .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                .select(new QPostImageDTO(
                        postImage.post.id,
                        postImage.id,
                        postImage.image.imageUrl
                ))
                .from(postImage)
                .where(postImage.post.id.in(postIds))
                .fetch();

        final List<Long> postImageIds = postImageDTOs.stream()
                .map(PostImageDTO::getId)
                .collect(Collectors.toList());

        final List<PostTagDTO> postTagDTOs = queryFactory
                .select(new QPostTagDTO(
                        postTag.postImage.id,
                        postTag.id,
                        postTag.tag
                ))
                .from(postTag)
                .where(postTag.postImage.id.in(postImageIds))
                .fetch();

        final Map<Long, List<PostTagDTO>> postImageDTOMap = postTagDTOs.stream()
                .collect(Collectors.groupingBy(PostTagDTO::getPostImageId));
        postImageDTOs.forEach(i -> i.setPostTagDTOs(postImageDTOMap.get(i.getId())));

        final Map<Long, List<PostImageDTO>> postDTOMap = postImageDTOs.stream()
                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
        postDTOs.forEach(p -> p.setPostImageDTOs(postDTOMap.get(p.getPostId())));

        return new PageImpl<>(postDTOs, pageable, postDTOs.size());
    }

    @Override
    public List<PostDTO> findRecent10PostDTOs(Long memberId) {
        final List<PostDTO> postDTOs = queryFactory
                .select(new QPostDTO(
                        post.id,
                        post.content,
                        post.uploadDate,
                        post.member.username,
                        post.member.name,
                        post.member.image.imageUrl,
                        post.comments.size(),
                        post.postLikes.size(),
                        JPAExpressions
                                .selectFrom(bookmark)
                                .where(bookmark.post.eq(post).and(bookmark.member.id.eq(memberId)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(postLike)
                                .where(postLike.post.eq(post).and(postLike.member.id.eq(memberId)))
                                .exists(),
                        JPAExpressions
                                .select(postLike.member.username)
                                .from(postLike)
                                .where(postLike.post.eq(post)
                                        .and(postLike.member
                                                .in(JPAExpressions
                                                        .select(follow.followMember)
                                                        .from(follow)
                                                        .where(follow.member.id.eq(memberId)))))
                                .limit(1)
                ))
                .from(post)
                .join(post.member, QMember.member)
                .on(post.member.username.in(
                        JPAExpressions
                                .select(follow.followMember.username)
                                .from(follow)
                                .where(follow.member.id.eq(memberId))
                ))
                .limit(10)
                .orderBy(post.id.desc())
                .fetch();

        final List<Long> postIds = postDTOs.stream()
                .map(PostDTO::getPostId)
                .collect(Collectors.toList());

        final List<PostImageDTO> postImageDTOs = queryFactory
                .select(new QPostImageDTO(
                        postImage.post.id,
                        postImage.id,
                        postImage.image.imageUrl
                ))
                .from(postImage)
                .where(postImage.post.id.in(postIds))
                .fetch();

        final List<Long> postImageIds = postImageDTOs.stream()
                .map(PostImageDTO::getId)
                .collect(Collectors.toList());

        final List<PostTagDTO> postTagDTOs = queryFactory
                .select(new QPostTagDTO(
                        postTag.postImage.id,
                        postTag.id,
                        postTag.tag
                ))
                .from(postTag)
                .where(postTag.postImage.id.in(postImageIds))
                .fetch();

        final Map<Long, List<PostTagDTO>> postImageDTOMap = postTagDTOs.stream()
                .collect(Collectors.groupingBy(PostTagDTO::getPostImageId));
        postImageDTOs.forEach(i -> i.setPostTagDTOs(postImageDTOMap.get(i.getId())));

        final Map<Long, List<PostImageDTO>> postDTOMap = postImageDTOs.stream()
                .collect(Collectors.groupingBy(PostImageDTO::getPostId));
        postDTOs.forEach(p -> p.setPostImageDTOs(postDTOMap.get(p.getPostId())));

        return postDTOs;
    }

}
