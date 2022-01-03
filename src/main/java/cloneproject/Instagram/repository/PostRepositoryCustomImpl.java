package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.dto.post.PostImageDTO;
import cloneproject.Instagram.dto.post.PostTagDTO;
import cloneproject.Instagram.dto.post.QPostDTO;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.member.QMember;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

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
        QueryResults<PostDTO> results = queryFactory
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
                )).distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetchResults();

        List<PostDTO> content = results.getResults();
        for (PostDTO postDTO : content) {
            final List<PostImageDTO> transform = queryFactory
                    .from(postImage)
                    .where(postImage.post.id.eq(postDTO.getPostId()))
                    .innerJoin(postTag)
                    .on(postImage.id.eq(postTag.postImage.id))
                    .transform(
                            groupBy(postImage.id).list(
                                    Projections.fields(
                                            PostImageDTO.class,
                                            postImage.id,
                                            postImage.image,
                                            list(
                                                    Projections.fields(
                                                            PostTagDTO.class,
                                                            postTag.id,
                                                            postTag.tag
                                                    )
                                            ).as("postTagDTOs")
                                    )
                            )
                    );
            postDTO.setPostImageDTOs(transform);
        }

        return new PageImpl<>(content, pageable, content.size());
    }
}
