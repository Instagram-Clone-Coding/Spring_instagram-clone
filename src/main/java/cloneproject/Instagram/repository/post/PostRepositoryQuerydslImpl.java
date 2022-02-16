package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.dto.comment.CommentDTO;
import cloneproject.Instagram.dto.comment.QCommentDTO;
import cloneproject.Instagram.dto.post.*;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.member.QMember;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cloneproject.Instagram.entity.comment.QComment.comment;
import static cloneproject.Instagram.entity.comment.QCommentLike.commentLike;
import static cloneproject.Instagram.entity.comment.QRecentComment.recentComment;
import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.post.QBookmark.bookmark;
import static cloneproject.Instagram.entity.post.QPost.post;
import static cloneproject.Instagram.entity.post.QPostImage.postImage;
import static cloneproject.Instagram.entity.post.QPostLike.postLike;
import static cloneproject.Instagram.entity.post.QPostTag.postTag;

@Slf4j
@RequiredArgsConstructor
public class PostRepositoryQuerydslImpl implements PostRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostDTO> findPostDtoPage(Member member, Pageable pageable) {
        if (member.getFollowings().isEmpty())
            return null;

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
                                .exists()
                ))
                .from(post)
                .innerJoin(post.member, QMember.member)
                .where(post.member.username.in(
                        JPAExpressions
                                .select(follow.followMember.username)
                                .from(follow)
                                .where(follow.member.eq(member))
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .distinct()
                .fetch();

        final long total = queryFactory
                .selectFrom(post)
                .innerJoin(post.member, QMember.member).fetchJoin()
                .where(post.member.username.in(
                        JPAExpressions
                                .select(follow.followMember.username)
                                .from(follow)
                                .where(follow.member.eq(member))))
                .fetchCount();

        final List<Long> postIds = postDTOs.stream()
                .map(PostDTO::getPostId)
                .collect(Collectors.toList());

        final Map<Long, List<CommentDTO>> recentCommentMap = queryFactory
                .select(new QCommentDTO(
                        recentComment.post.id,
                        recentComment.comment.id,
                        recentComment.member,
                        recentComment.comment.content,
                        recentComment.comment.uploadDate,
                        recentComment.comment.commentLikes.size(),
                        JPAExpressions
                                .selectFrom(commentLike)
                                .where(commentLike.comment.eq(comment).and(commentLike.member.id.eq(member.getId())))
                                .exists(),
                        recentComment.comment.children.size()
                ))
                .from(recentComment)
                .innerJoin(recentComment.comment, comment)
                .innerJoin(recentComment.member, QMember.member)
                .where(recentComment.post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(CommentDTO::getPostId));
        postDTOs.forEach(p -> p.setRecentComments(recentCommentMap.get(p.getPostId())));

        final List<PostLikeDTO> postLikeDTOs = queryFactory
                .select(new QPostLikeDTO(
                        postLike.post.id,
                        postLike.member.username
                ))
                .from(postLike)
                .innerJoin(postLike.member, QMember.member)
                .where(postLike.post.id.in(postIds)
                        .and(postLike.member.in(
                                JPAExpressions
                                        .select(follow.followMember)
                                        .from(follow)
                                        .innerJoin(follow.member, QMember.member)
                                        .innerJoin(follow.followMember, QMember.member)
                                        .where(follow.member.eq(member)))))
                .fetch();
        final Map<Long, List<PostLikeDTO>> postLikeDTOMap = postLikeDTOs.stream()
                .collect(Collectors.groupingBy(PostLikeDTO::getPostId));
        postDTOs.forEach(p -> p.setFollowingMemberUsernameLikedPost(postLikeDTOMap.containsKey(p.getPostId()) ? postLikeDTOMap.get(p.getPostId()).get(0).getUsername() : ""));

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

        return new PageImpl<>(postDTOs, pageable, total);
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
                                .exists()
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

        final Map<Long, List<CommentDTO>> recentCommentMap = queryFactory
                .select(new QCommentDTO(
                        recentComment.post.id,
                        recentComment.comment.id,
                        recentComment.member,
                        recentComment.comment.content,
                        recentComment.comment.uploadDate,
                        recentComment.comment.commentLikes.size(),
                        JPAExpressions
                                .selectFrom(commentLike)
                                .where(commentLike.comment.eq(comment).and(commentLike.member.id.eq(memberId)))
                                .exists(),
                        recentComment.comment.children.size()
                ))
                .from(recentComment)
                .innerJoin(recentComment.comment, comment)
                .innerJoin(recentComment.member, QMember.member)
                .where(recentComment.post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(CommentDTO::getPostId));
        postDTOs.forEach(p -> p.setRecentComments(recentCommentMap.get(p.getPostId())));

        final List<PostLikeDTO> postLikeDTOs = queryFactory
                .select(new QPostLikeDTO(
                        postLike.post.id,
                        postLike.member.username
                ))
                .from(postLike)
                .innerJoin(postLike.member, QMember.member)
                .where(postLike.post.id.in(postIds)
                        .and(postLike.member.in(
                                JPAExpressions
                                        .select(follow.followMember)
                                        .from(follow)
                                        .innerJoin(follow.member, QMember.member)
                                        .innerJoin(follow.followMember, QMember.member)
                                        .on(follow.member.id.eq(memberId)))))
                .fetch();
        final Map<Long, List<PostLikeDTO>> postLikeDTOMap = postLikeDTOs.stream()
                .collect(Collectors.groupingBy(PostLikeDTO::getPostId));
        postDTOs.forEach(p -> p.setFollowingMemberUsernameLikedPost(postLikeDTOMap.containsKey(p.getPostId()) ? postLikeDTOMap.get(p.getPostId()).get(0).getUsername() : ""));

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

    @Override
    public Optional<PostResponse> findPostResponse(Long postId, Long memberId) {
        final Optional<PostResponse> response = Optional.ofNullable(queryFactory
                .select(new QPostResponse(
                        post.id,
                        post.content,
                        post.uploadDate,
                        post.member.username,
                        post.member.name,
                        post.member.image.imageUrl,
                        post.postLikes.size(),
                        JPAExpressions
                                .selectFrom(bookmark)
                                .where(bookmark.post.eq(post).and(bookmark.member.id.eq(memberId)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(postLike)
                                .where(postLike.post.eq(post).and(postLike.member.id.eq(memberId)))
                                .exists()
                ))
                .from(post)
                .where(post.id.eq(postId))
                .fetchOne());

        if (response.isEmpty())
            return Optional.empty();

        final List<PostLikeDTO> postLikeDTOs = queryFactory
                .select(new QPostLikeDTO(
                        postLike.post.id,
                        postLike.member.username
                ))
                .from(postLike)
                .innerJoin(postLike.member, QMember.member)
                .where(postLike.post.id.eq(postId)
                        .and(postLike.member.in(
                                JPAExpressions
                                        .select(follow.followMember)
                                        .from(follow)
                                        .innerJoin(follow.member, QMember.member)
                                        .innerJoin(follow.followMember, QMember.member)
                                        .where(follow.member.id.eq(memberId))
                        )))
                .fetch();
        response.get().setFollowingMemberUsernameLikedPost(postLikeDTOs.isEmpty() ? "" : postLikeDTOs.get(0).getUsername());

        final List<PostImageDTO> postImageDTOs = queryFactory
                .select(new QPostImageDTO(
                        postImage.post.id,
                        postImage.id,
                        postImage.image.imageUrl
                ))
                .from(postImage)
                .where(postImage.post.id.eq(postId))
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

        response.get().setPostImageDTOs(postImageDTOs);

        final List<CommentDTO> commentDTOs = queryFactory
                .select(new QCommentDTO(
                        comment.post.id,
                        comment.id,
                        comment.member,
                        comment.content,
                        comment.uploadDate,
                        comment.commentLikes.size(),
                        JPAExpressions
                                .selectFrom(commentLike)
                                .where(commentLike.comment.eq(comment).and(commentLike.member.id.eq(memberId)))
                                .exists(),
                        comment.children.size()
                ))
                .from(comment)
                .where(comment.post.id.eq(postId).and(comment.id.eq(comment.parent.id)))
                .innerJoin(comment.member, QMember.member)
                .orderBy(comment.id.desc())
                .limit(10)
                .fetch();

        response.get().setCommentDTOs(commentDTOs);

        return response;
    }

}
