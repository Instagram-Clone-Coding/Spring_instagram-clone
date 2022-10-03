package cloneproject.Instagram.domain.feed.repository;

import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.config.QuerydslConfig;
import cloneproject.Instagram.util.domain.feed.PostUtils;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@DataJpaTest
@Import(QuerydslConfig.class)
class PostRepositoryTest {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Nested
	class FindWithMemberById {

		@Test
		void validPostId_ReturnPost() throws Exception {
			// given
			final Member member = memberRepository.save(MemberUtils.newInstance());
			final Post post = PostUtils.newInstance(member);
			final Long postId = postRepository.save(post).getId();

			// when
			final boolean isPresent = postRepository.findWithMemberById(postId).isPresent();

			// then
			assertThat(isPresent).isTrue();
		}

		@Test
		void validPostId_ReturnPostWithMember() throws Exception {
			// given
			final Member member = memberRepository.save(MemberUtils.newInstance());
			final Post post = PostUtils.newInstance(member);
			final Long postId = postRepository.save(post).getId();

			// when
			final Member fetchMember = postRepository.findWithMemberById(postId).get().getMember();

			// then
			assertThat(fetchMember.getId()).isEqualTo(member.getId());
		}

		@Test
		void invalidPostId_ReturnEmpty() throws Exception {
			// given
			final Long postId = 152314L;

			// when
			final boolean isEmpty = postRepository.findWithMemberById(postId).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

	@Nested
	class FindTop3ByMemberIdOrderByIdDesc {

		@Test
		void MemberUploaded5Posts_ReturnTop3Posts() throws Exception {
		    // given
			final Member member = memberRepository.save(MemberUtils.newInstance());
			final long postCounts = 5L;
			final List<Post> posts = PostUtils.newInstances(member, postCounts);
			postRepository.saveAll(posts);
			final long top3 = 3L;

			// when
			final int total = postRepository.findTop3ByMemberIdOrderByIdDesc(member.getId()).size();

			// then
			assertThat(total).isEqualTo(top3);
		}

		@Test
		void MemberUploaded5Posts_OrderByIdDesc() throws Exception {
		    // given
			final Member member = memberRepository.save(MemberUtils.newInstance());
			final long postCounts = 5L;
			final List<Post> posts = postRepository.saveAll(PostUtils.newInstances(member, postCounts));
			final long top3 = 3L;
			final List<Long> top3PostIds = posts.stream()
				.sorted(comparing(Post::getId).reversed())
				.limit(top3)
				.map(Post::getId)
				.collect(Collectors.toList());

			// when
			final List<Long> result = postRepository.findTop3ByMemberIdOrderByIdDesc(member.getId()).stream()
				.map(Post::getId)
				.collect(Collectors.toList());

			// then
			assertThat(top3PostIds).hasSameElementsAs(result);
		}

		@Test
		void MemberUploadedNothing_ReturnEmpty() throws Exception {
		    // given
			final Member member = memberRepository.save(MemberUtils.newInstance());

			// when
			final boolean isEmpty = postRepository.findTop3ByMemberIdOrderByIdDesc(member.getId()).isEmpty();

			// then
			assertThat(isEmpty).isTrue();
		}

	}

}
