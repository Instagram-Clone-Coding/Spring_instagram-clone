package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.feed.dto.MiniProfilePostDto;
import cloneproject.Instagram.domain.feed.dto.PostImageDto;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.dto.EditProfileRequest;
import cloneproject.Instagram.domain.member.dto.EditProfileResponse;
import cloneproject.Instagram.domain.member.dto.MenuMemberProfile;
import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.entity.Gender;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.UsernameAlreadyExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.infra.aws.S3Uploader;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private static final int MAX_PROFILE_FOLLOWING_MEMBER_FOLLOW_COUNT = 3;
	private static final int MAX_MINI_PROFILE_FOLLOWING_MEMBER_FOLLOW_COUNT = 1;
	private static final String MEMBER_S3_DIRNAME = "member";
	private final AuthUtil authUtil;
	private final MemberRepository memberRepository;
	private final FollowRepository followRepository;
	private final S3Uploader s3Uploader;
	private final MemberStoryRedisRepository memberStoryRedisRepository;
	private final PostRepository postRepository;
	private final PostImageRepository postImageRepository;

	public MenuMemberProfile getMenuMemberProfile() {
		final Member member = authUtil.getLoginMember();
		return new MenuMemberProfile(member);
	}

	public UserProfileResponse getUserProfile(String username) {
		final Long memberId = authUtil.getLoginMemberId();
		return getUserProfile(username, memberId);
	}

	public UserProfileResponse getUserProfileWithoutLogin(String username) {
		return getUserProfile(username, -1L);
	}

	public MiniProfileResponse getMiniProfile(String username) {
		final Long memberId = authUtil.getLoginMemberId();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		final MiniProfileResponse result = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(memberId, member.getUsername());
		final List<FollowDto> followDtos = followRepository.findFollowingMemberFollowList(memberId, username);

		result.setFollowingMemberFollow(followDtos, MAX_MINI_PROFILE_FOLLOWING_MEMBER_FOLLOW_COUNT);
		result.setHasStory(memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0);
		setMemberPostImages(result, member.getId());
		return result;
	}

	@Transactional
	public void uploadMemberImage(MultipartFile uploadedImage) {
		final Member member = authUtil.getLoginMember();

		// 기존 사진 삭제
		final Image originalImage = member.getImage();
		s3Uploader.deleteImage(originalImage, MEMBER_S3_DIRNAME);

		final Image image = s3Uploader.uploadImage(uploadedImage, MEMBER_S3_DIRNAME);
		member.uploadImage(image);
		memberRepository.save(member);
	}

	@Transactional
	public void deleteMemberImage() {
		final Member member = authUtil.getLoginMember();
		s3Uploader.deleteImage(member.getImage(), MEMBER_S3_DIRNAME);
		member.deleteImage();
		memberRepository.save(member);
	}

	public EditProfileResponse getEditProfile() {
		final Member member = authUtil.getLoginMember();
		return new EditProfileResponse(member);
	}

	@Transactional
	public void editProfile(EditProfileRequest editProfileRequest) {
		final Member member = authUtil.getLoginMember();

		if (memberRepository.existsByUsername(editProfileRequest.getMemberUsername())
			&& !member.getUsername().equals(editProfileRequest.getMemberUsername())) {
			throw new UsernameAlreadyExistException();
		}

		updateMemberProfile(member, editProfileRequest);
	}

	private UserProfileResponse getUserProfile(String username, Long memberId) {
		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		final UserProfileResponse result = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(memberId, member.getUsername());
		final List<FollowDto> followDtos = followRepository.findFollowingMemberFollowList(memberId, username);

		result.setFollowingMemberFollow(followDtos, MAX_PROFILE_FOLLOWING_MEMBER_FOLLOW_COUNT);
		result.setHasStory(memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0);

		return result;
	}

	private void setMemberPostImages(MiniProfileResponse miniProfileResponse, Long memberId) {
		final List<Post> posts = postRepository.findTop3ByMemberIdOrderByIdDesc(memberId);
		final List<Long> postIds = posts.stream()
			.map(Post::getId)
			.collect(Collectors.toList());
		final List<PostImageDto> postImages = postImageRepository.findAllPostImageDtoByPostIdIn(postIds);

		final Map<Long, List<PostImageDto>> postDTOMap = postImages.stream()
			.collect(Collectors.groupingBy(PostImageDto::getPostId));

		final List<MiniProfilePostDto> results = new ArrayList<>();
		postDTOMap.forEach((id, p) -> results.add(
			MiniProfilePostDto.builder()
				.postId(id)
				.postImageUrl(p.get(0).getPostImageUrl())
				.build()));

		miniProfileResponse.setMemberPosts(results);
	}

	private void updateMemberProfile(Member member, EditProfileRequest editProfileRequest) {
		member.updateUsername(editProfileRequest.getMemberUsername());
		member.updateName(editProfileRequest.getMemberName());
		member.updateEmail(editProfileRequest.getMemberEmail());
		member.updateIntroduce(editProfileRequest.getMemberIntroduce());
		member.updateWebsite(editProfileRequest.getMemberWebsite());
		member.updatePhone(editProfileRequest.getMemberPhone());
		member.updateGender(Gender.valueOf(editProfileRequest.getMemberGender()));
	}

}
