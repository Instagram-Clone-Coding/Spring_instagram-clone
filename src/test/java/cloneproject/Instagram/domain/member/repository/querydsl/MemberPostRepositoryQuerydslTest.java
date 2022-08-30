package cloneproject.Instagram.domain.member.repository.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Nested;
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

	private static final int FIRST_ELEMENT_INDEX = 0;

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

	@Nested
	class FindMemberPostDtoPage {

		@Test
		void total15PostsExistAndOnly10PostsUploadedByMember_Find10MemberPostDtos() {
			// given
			final long memberUploadedPostCount = 10;
			final long otherPostCount = 5;
			final int page = 0;
			final int pageSize = 15;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member anotherMember = MemberUtils.newInstance();
			memberRepository.save(anotherMember);

			final List<Post> otherPosts = PostUtils.newInstances(anotherMember, otherPostCount);
			postRepository.saveAll(otherPosts);

			final List<Post> memberUploadedPosts = PostUtils.newInstances(member, memberUploadedPostCount);
			postRepository.saveAll(memberUploadedPosts);

			prepareBookmarks(memberUploadedPosts, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(memberUploadedPostCount);
		}

		@Test
		void total20PostsExistAndPage0AndSize15_FindTotal20PostsAnd2PagesAndCurrent15PostsAnd0Page() {
			// given
			final long postCount = 20;
			final int page = 0;
			final int pageSize = 15;

			final int expectedTotalPage = 2;
			final int expectedCurrentPage = 0;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> posts = PostUtils.newInstances(member, postCount);
			postRepository.saveAll(posts);

			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(postCount);
			assertThat(memberPostDtoPage.getTotalPages()).isEqualTo(expectedTotalPage);
			assertThat(memberPostDtoPage.getContent().size()).isEqualTo(pageSize);
			assertThat(memberPostDtoPage.getNumber()).isEqualTo(expectedCurrentPage);
		}

		@Test
		void total20PostsExistAndPage1AndSize15_FindTotal20PostsAnd2PagesAndCurrent5PostsAnd1Page() {
			// given
			final long postCount = 20;
			final int page = 1;
			final int pageSize = 15;

			final int expectedTotalPage = 2;
			final int expectedCurrentPage = 1;
			final int expectedCurrentPostCount = 5;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> posts = PostUtils.newInstances(member, postCount);
			postRepository.saveAll(posts);

			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(postCount);
			assertThat(memberPostDtoPage.getTotalPages()).isEqualTo(expectedTotalPage);
			assertThat(memberPostDtoPage.getContent().size()).isEqualTo(expectedCurrentPostCount);
			assertThat(memberPostDtoPage.getNumber()).isEqualTo(expectedCurrentPage);
		}

		@Test
		void validArguments_AllFieldsMappedSuccess() {
			// given
			final long postImageCount = 1;
			final long postCommentCount = 3;
			final long postLikeCount = 3;
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			preparePostImagesAndCommentsAndLikes(post, member, postImageCount, postCommentCount, postLikeCount);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostId()).isEqualTo(post.getId());
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getMember().getId()).isEqualTo(
				member.getId());
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isHasManyPostImages()).isFalse();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostCommentsCount()).isEqualTo(
				postCommentCount);
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostLikesCount()).isEqualTo(
				postLikeCount);
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isPostLikeFlag()).isFalse();
		}

		@Test
		void postHas2Images_HasManyPostImagesFlagIsTrue() {
			// given
			final long postImageCount = 2;
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			preparePostImages(post, postImageCount);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isHasManyPostImages()).isTrue();
		}

		@Test
		void memberLikesPost_LikeFlagIsTrue() {
			// given
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			final PostLike postLike = PostLikeUtils.of(post, member);
			postLikeRepository.save(postLike);

			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberPostDtos(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isPostLikeFlag()).isTrue();
		}

	}

	@Nested
	class FindMemberSavedPostDtoPage {

		@Test
		void total15PostsExistAndOnly10PostsSaved_Find10MemberPostDtos() {
			// given
			final long unsavedPostCount = 5;
			final long savedPostCount = 10;
			final int page = 0;
			final int pageSize = 15;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> unsavedPosts = PostUtils.newInstances(member, unsavedPostCount);
			postRepository.saveAll(unsavedPosts);

			final List<Post> savedPosts = PostUtils.newInstances(member, savedPostCount);
			postRepository.saveAll(savedPosts);

			prepareBookmarks(savedPosts, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
				pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(savedPostCount);
		}

		@Test
		void total20PostsSavedAndPage1AndSize15_FindTotal20PostsAnd2PagesAndCurrent5PostsAnd1Page() {
			// given
			final long savedPostCount = 20;
			final int page = 0;
			final int pageSize = 15;

			final int expectedTotalPage = 2;
			final int expectedCurrentPage = 0;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> savedPosts = PostUtils.newInstances(member, savedPostCount);
			postRepository.saveAll(savedPosts);

			prepareBookmarks(savedPosts, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
				pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(savedPostCount);
			assertThat(memberPostDtoPage.getTotalPages()).isEqualTo(expectedTotalPage);
			assertThat(memberPostDtoPage.getContent().size()).isEqualTo(pageSize);
			assertThat(memberPostDtoPage.getNumber()).isEqualTo(expectedCurrentPage);
		}

		@Test
		void total20PostsSavedAndPage0AndSize15_FindTotal20PostsAnd2PagesAndCurrent15PostsAnd0Page() {
			// given
			final long savedPostCount = 20;
			final int page = 1;
			final int pageSize = 15;

			final int expectedTotalPage = 2;
			final int expectedCurrentPage = 1;
			final int expectedCurrentPostCount = 5;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> savedPosts = PostUtils.newInstances(member, savedPostCount);
			postRepository.saveAll(savedPosts);

			prepareBookmarks(savedPosts, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
				pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(savedPostCount);
			assertThat(memberPostDtoPage.getTotalPages()).isEqualTo(expectedTotalPage);
			assertThat(memberPostDtoPage.getContent().size()).isEqualTo(expectedCurrentPostCount);
			assertThat(memberPostDtoPage.getNumber()).isEqualTo(expectedCurrentPage);
		}

		@Test
		void validArguments_AllFieldsMappedSuccess() {
			// given
			final long postImageCount = 1;
			final long postCommentCount = 3;
			final long postLikeCount = 3;
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			prepareBookmark(post, member);
			preparePostImagesAndCommentsAndLikes(post, member, postImageCount, postCommentCount, postLikeCount);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
				pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostId()).isEqualTo(post.getId());
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getMember().getId()).isEqualTo(
				member.getId());
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isHasManyPostImages()).isFalse();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostCommentsCount()).isEqualTo(
				postCommentCount);
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostLikesCount()).isEqualTo(
				postLikeCount);
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isPostLikeFlag()).isFalse();
		}

		@Test
		void postHas2Images_FindWithHasManyPostImagesFlagTrue() {
			// given
			final long postImageCount = 2;
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			prepareBookmark(post, member);
			preparePostImages(post, postImageCount);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
				pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isHasManyPostImages()).isTrue();
		}

		@Test
		void memberLikesPost_FindMemberPostDtoWithLikeFlagTrue() {
			// given
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			final PostLike postLike = PostLikeUtils.of(post, member);
			postLikeRepository.save(postLike);
			prepareBookmark(post, member);

			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberSavedPostDtoPage(member.getId(),
				pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isPostLikeFlag()).isTrue();
		}

	}

	@Nested
	class FindMemberTaggedPostDtoPage {

		@Test
		void total15PostsExistAndOnly10PostsTagged_Find10MemberPostDtos() {
			// given
			final long untaggedPostCount = 5;
			final long taggedPostCount = 10;
			final long postImageCount = 1;
			final int page = 0;
			final int pageSize = 15;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> untaggedPosts = PostUtils.newInstances(member, untaggedPostCount);
			postRepository.saveAll(untaggedPosts);

			final List<Post> taggedPosts = PostUtils.newInstances(member, taggedPostCount);
			postRepository.saveAll(taggedPosts);

			final List<PostImage> postImages = PostImageUtils.newInstancesForEachPost(taggedPosts, postImageCount);
			postImageRepository.saveAll(postImages);

			preparePostTags(postImages, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(taggedPostCount);
		}

		@Test
		void total20PostsTaggedAndPage0AndSize15_FindTotal20PostsAnd2PagesAndCurrent15PostsAnd0Page() {
			// given
			final long taggedPostCount = 20;
			final long postImageCount = 1;
			final int page = 0;
			final int pageSize = 15;

			final int expectedTotalPage = 2;
			final int expectedCurrentPage = 0;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> taggedPosts = PostUtils.newInstances(member, taggedPostCount);
			postRepository.saveAll(taggedPosts);

			final List<PostImage> postImages = PostImageUtils.newInstancesForEachPost(taggedPosts, postImageCount);
			postImageRepository.saveAll(postImages);

			preparePostTags(postImages, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(taggedPostCount);
			assertThat(memberPostDtoPage.getTotalPages()).isEqualTo(expectedTotalPage);
			assertThat(memberPostDtoPage.getContent().size()).isEqualTo(pageSize);
			assertThat(memberPostDtoPage.getNumber()).isEqualTo(expectedCurrentPage);
		}

		@Test
		void total20PostsTaggedAndPage1AndSize15_FindTotal20PostsAnd2PagesAndCurrent5PostsAnd1Page() {
			// given
			final long taggedPostCount = 20;
			final long postImageCount = 1;
			final int page = 1;
			final int pageSize = 15;

			final int expectedTotalPage = 2;
			final int expectedCurrentPage = 1;
			final int expectedCurrentPostCount = 5;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final List<Post> taggedPosts = PostUtils.newInstances(member, taggedPostCount);
			postRepository.saveAll(taggedPosts);

			final List<PostImage> postImages = PostImageUtils.newInstancesForEachPost(taggedPosts, postImageCount);
			postImageRepository.saveAll(postImages);

			preparePostTags(postImages, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getTotalElements()).isEqualTo(taggedPostCount);
			assertThat(memberPostDtoPage.getTotalPages()).isEqualTo(expectedTotalPage);
			assertThat(memberPostDtoPage.getContent().size()).isEqualTo(expectedCurrentPostCount);
			assertThat(memberPostDtoPage.getNumber()).isEqualTo(expectedCurrentPage);
		}

		@Test
		void validArguments_AllFieldsMappedSuccess() {
			// given
			final long postImageCount = 1;
			final long postCommentCount = 4;
			final long postLikeCount = 5;
			final int page = 0;
			final int pageSize = 15;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			final List<PostImage> postImages = preparePostImagesAndCommentsAndLikes(post, member, postImageCount,
				postCommentCount, postLikeCount);

			preparePostTags(postImages, member);
			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostId()).isEqualTo(post.getId());
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getMember().getId()).isEqualTo(
				member.getId());
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isHasManyPostImages()).isFalse();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostCommentsCount()).isEqualTo(
				postCommentCount);
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).getPostLikesCount()).isEqualTo(
				postLikeCount);
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isPostLikeFlag()).isFalse();
		}

		@Test
		void postHas2Images_HasManyPostImagesFlagIsTrue() {
			// given
			final long postImageCount = 2;
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			final List<PostImage> postImages = preparePostImages(post, postImageCount);
			preparePostTags(postImages, member);

			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isHasManyPostImages()).isTrue();
		}

		@Test
		void memberLikesPost_FindMemberPostDtoWithLikeFlagTrue() {
			// given
			final int page = 0;
			final int pageSize = 1;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);

			final PostLike postLike = PostLikeUtils.of(post, member);
			postLikeRepository.save(postLike);

			final PostImage postImage = PostImageUtils.newInstance(post);
			postImageRepository.save(postImage);

			final PostTag postTag = PostTagUtils.newInstance(postImage, member.getUsername());
			postTagRepository.save(postTag);

			final Pageable pageable = PageRequest.of(page, pageSize);

			// when
			final Page<MemberPostDto> memberPostDtoPage = memberRepository.findMemberTaggedPostDtoPage(member.getId(),
				member.getUsername(), pageable);

			// then
			assertThat(memberPostDtoPage.getContent().size()).isNotZero();
			assertThat(memberPostDtoPage.getContent().get(FIRST_ELEMENT_INDEX).isPostLikeFlag()).isTrue();
		}

	}

	private List<PostImage> preparePostImagesAndCommentsAndLikes(Post post, Member member, long postImageCount,
		long postCommentCount, long postLikeCount) {
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

	private List<PostImage> preparePostImages(Post post, long postImageCount) {
		final List<PostImage> postImages = PostImageUtils.newInstances(post, postImageCount);
		postImageRepository.saveAll(postImages);

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
