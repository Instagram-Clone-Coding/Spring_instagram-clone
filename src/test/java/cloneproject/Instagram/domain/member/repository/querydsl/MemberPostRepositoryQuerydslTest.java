package cloneproject.Instagram.domain.member.repository.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

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
import cloneproject.Instagram.util.domain.feed.PostLikeUtils;
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
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final List<Post> posts = PostUtils.newInstances(member, postCount);
		postRepository.saveAll(posts);

		// when
		final Pageable pageable = PageRequest.of(0, 15);
		final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
			member.getUsername(), pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isEqualTo(postCount);
	}

	@Test
	void findMemberPostDtos_PostHasDetail_FindMemberPostDtoWithDetail() {
		// given
		final long postImageCount = 2;
		final long postCommentCount = 3;
		final long postLikeCount = 3;

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Post post = PostUtils.newInstance(member);
		postRepository.save(post);

		preparePostDetail(post, member, postImageCount, postCommentCount, postLikeCount);

		// when
		final Pageable pageable = PageRequest.of(0, 1);
		final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
			member.getUsername(), pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isNotZero();
		assertThat(memberPostDtoPage.getContent().get(0).isHasManyPostImages()).isTrue();
		assertThat(memberPostDtoPage.getContent().get(0).getPostCommentsCount()).isEqualTo(postCommentCount);
		assertThat(memberPostDtoPage.getContent().get(0).getPostLikesCount()).isEqualTo(postLikeCount);
		assertThat(memberPostDtoPage.getContent().get(0).isPostLikeFlag()).isFalse();
	}

	@Test
	void findMemberSavedPostDtoPage_MemberSaved3Posts_Find3MemberPostDtos() {
		// given
		final long postCount = 3;
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final List<Post> posts = PostUtils.newInstances(member, postCount);
		postRepository.saveAll(posts);

		prepareBookmarks(posts, member);

		// when
		final Pageable pageable = PageRequest.of(0, 15);
		final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
			pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isEqualTo(postCount);
	}

	@Test
	void findMemberSavedPostDtoPage_PostHasDetail_FindMemberPostDtoWithDetail() {
		// given
		final long postImageCount = 2;
		final long postCommentCount = 3;
		final long postLikeCount = 3;

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Post post = PostUtils.newInstance(member);
		postRepository.save(post);

		prepareBookmark(post, member);
		preparePostDetail(post, member, postImageCount, postCommentCount, postLikeCount);

		// when
		final Pageable pageable = PageRequest.of(0, 1);
		final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
			pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isNotZero();
		assertThat(memberPostDtoPage.getContent().get(0).isHasManyPostImages()).isTrue();
		assertThat(memberPostDtoPage.getContent().get(0).getPostCommentsCount()).isEqualTo(postCommentCount);
		assertThat(memberPostDtoPage.getContent().get(0).getPostLikesCount()).isEqualTo(postLikeCount);
		assertThat(memberPostDtoPage.getContent().get(0).isPostLikeFlag()).isFalse();
	}

	@Test
	void findMemberTaggedPostDtoPage_MemberTagged3Posts_Find3MemberPostDtos() {
		// given
		final long postCount = 3;
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final List<Post> posts = PostUtils.newInstances(member, postCount);
		postRepository.saveAll(posts);

		final List<PostImage> postImages = PostImageUtils.newInstancesForEachPost(posts, 1);
		postImageRepository.saveAll(postImages);
		preparePostTags(postImages, member);

		// when
		final Pageable pageable = PageRequest.of(0, 15);
		final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
			member.getUsername(), pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isEqualTo(postCount);
	}

	@Test
	void findMemberTaggedPostDtoPage_PostHasDetail_FindMemberPostDtoWithDetail() {
		// given
		final long postImageCount = 3;
		final long postCommentCount = 4;
		final long postLikeCount = 5;

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Post post = PostUtils.newInstance(member);
		postRepository.save(post);

		final List<PostImage> postImages = preparePostDetail(post, member, postImageCount, postCommentCount, postLikeCount);
		preparePostTags(postImages, member);

		// when
		final Pageable pageable = PageRequest.of(0, 1);
		final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
			member.getUsername(), pageable);

		// then
		assertThat(memberPostDtoPage.getContent().size()).isNotZero();
		assertThat(memberPostDtoPage.getContent().get(0).isHasManyPostImages()).isTrue();
		assertThat(memberPostDtoPage.getContent().get(0).getPostCommentsCount()).isEqualTo(postCommentCount);
		assertThat(memberPostDtoPage.getContent().get(0).getPostLikesCount()).isEqualTo(postLikeCount);
		assertThat(memberPostDtoPage.getContent().get(0).isPostLikeFlag()).isFalse();
	}

	private List<PostImage> preparePostDetail(Post post, Member member, long postImageCount, long postCommentCount,
		long postLikeCount) {
		final List<PostImage> postImages = PostImageUtils.newInstances(post, postImageCount);
		postImageRepository.saveAll(postImages);

		final List<Comment> comments = CommentUtils.newInstances(post, member, postCommentCount);
		commentRepository.saveAll(comments);

		final List<Member> members = MemberUtils.newDistinctInstances(postLikeCount);
		memberRepository.saveAll(members);

		final List<PostLike> postLikes = PostLikeUtils.newInstancesForEachMember(post, members);
		postLikeRepository.saveAll(postLikes);

		return postImages;
	}

	private void prepareBookmarks(List<Post> posts, Member member) {
		for (Post post : posts) {
			prepareBookmark(post, member);
		}
	}

	private void preparePostTags(List<PostImage> postImages, Member member) {
		final List<PostTag> postTags = PostTagUtils.newInstancesForEachPostImage(postImages, member.getUsername());
		postTagRepository.saveAll(postTags);
	}

	private void prepareBookmark(Post post, Member member) {
		final Bookmark bookmark = Bookmark.builder()
			.member(member)
			.post(post)
			.build();
		bookmarkRepository.save(bookmark);
	}

}