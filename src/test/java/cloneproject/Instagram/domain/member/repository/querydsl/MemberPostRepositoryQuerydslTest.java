package cloneproject.Instagram.domain.member.repository.querydsl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.feed.entity.Bookmark;
import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.domain.feed.entity.PostLike;
import cloneproject.Instagram.domain.feed.entity.PostTag;
import cloneproject.Instagram.domain.feed.repository.BookmarkRepository;
import cloneproject.Instagram.domain.feed.repository.CommentRepository;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.feed.repository.PostLikeRepository;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.feed.repository.PostTagRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.config.QuerydslConfig;
import cloneproject.Instagram.util.domain.feed.CommentUtils;
import cloneproject.Instagram.util.domain.feed.PostImageUtils;
import cloneproject.Instagram.util.domain.feed.PostTagUtils;
import cloneproject.Instagram.util.domain.feed.PostUtils;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@DataJpaTest
@Import(QuerydslConfig.class)
class MemberPostRepositoryQuerydslTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostImageRepository postImageRepository;

	@Autowired
	private PostTagRepository postTagRepository;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Test
	void findMemberPostDtos_MemberUploaded3Posts_Find3MemberPostDtos() {
		// given
		final long postCount = 3;
		final long postImageCount = 2;
		final long postCommentCount = 3;
		final long postLikeCount = 3;
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		preparePosts(member, postCount, postImageCount, postCommentCount, postLikeCount);

		// when
		Pageable pageable = PageRequest.of(0, 15);
		Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
			member.getUsername(), pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isEqualTo(postCount);
		assertThat(memberPostDtoPage.getContent().get(0).isHasManyPosts()).isTrue();
		assertThat(memberPostDtoPage.getContent().get(0).getPostCommentsCount()).isEqualTo(postCommentCount);
		assertThat(memberPostDtoPage.getContent().get(0).getPostLikesCount()).isEqualTo(postLikeCount);
		assertThat(memberPostDtoPage.getContent().get(0).isPostLikeFlag()).isFalse();
	}

	@Test
	void findMemberSavedPostDtoPage_MemberSaved3Posts_Find3MemberPostDtos() {
		final long postCount = 3;
		final long postImageCount = 3;
		final long postCommentCount = 4;
		final long postLikeCount = 2;
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		preparePosts(member, postCount, postImageCount, postCommentCount, postLikeCount);

		// when
		Pageable pageable = PageRequest.of(0, 15);
		Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(), pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isEqualTo(postCount);
		assertThat(memberPostDtoPage.getContent().get(0).isHasManyPosts()).isTrue();
		assertThat(memberPostDtoPage.getContent().get(0).getPostCommentsCount()).isEqualTo(postCommentCount);
		assertThat(memberPostDtoPage.getContent().get(0).getPostLikesCount()).isEqualTo(postLikeCount);
		assertThat(memberPostDtoPage.getContent().get(0).isPostLikeFlag()).isFalse();
	}

	@Test
	void findMemberTaggedPostDtoPage_MemberTagged3Posts_Find3MemberPostDtos() {
		// given
		final long postCount = 3;
		final long postImageCount = 3;
		final long postCommentCount = 4;
		final long postLikeCount = 2;
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		preparePosts(member, postCount, postImageCount, postCommentCount, postLikeCount);

		// when
		Pageable pageable = PageRequest.of(0, 15);
		Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
			member.getUsername(), pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isEqualTo(postCount);
		assertThat(memberPostDtoPage.getContent().get(0).isHasManyPosts()).isTrue();
		assertThat(memberPostDtoPage.getContent().get(0).getPostCommentsCount()).isEqualTo(postCommentCount);
		assertThat(memberPostDtoPage.getContent().get(0).getPostLikesCount()).isEqualTo(postLikeCount);
		assertThat(memberPostDtoPage.getContent().get(0).isPostLikeFlag()).isFalse();
	}

	private void preparePosts(Member member, long postCount, long postImageCount, long postCommentCount,
		long postLikeCount) {
		for (long count = 1; count <= postCount; count++) {
			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			final Bookmark bookmark = Bookmark.builder()
				.member(member)
				.post(post)
				.build();
			bookmarkRepository.save(bookmark);

			for (long imageCount = 1; imageCount <= postImageCount; imageCount++) {
				final PostImage postImage = PostImageUtils.newInstance(post);
				postImageRepository.save(postImage);

				final PostTag postTag = PostTagUtils.newInstance(postImage, member.getUsername());
				postTagRepository.save(postTag);
			}

			for (long commentCount = 1; commentCount <= postCommentCount; commentCount++) {
				final Comment comment = CommentUtils.newInstance(post, member);
				commentRepository.save(comment);
			}

			for (long likeCount = 1; likeCount <= postLikeCount; likeCount++) {
				final Member likeMember = MemberUtils.newInstance();
				memberRepository.save(likeMember);
				final PostLike postLike = PostLike.builder()
					.post(post)
					.member(likeMember)
					.build();
				postLikeRepository.save(postLike);
			}
		}
	}

}