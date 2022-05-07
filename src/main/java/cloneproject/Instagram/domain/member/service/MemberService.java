package cloneproject.Instagram.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.domain.feed.dto.MiniProfilePostDTO;
import cloneproject.Instagram.domain.feed.dto.PostImageDTO;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.member.dto.EditProfileRequest;
import cloneproject.Instagram.domain.member.dto.EditProfileResponse;
import cloneproject.Instagram.domain.member.dto.MenuMemberDTO;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cloneproject.Instagram.global.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final AuthUtil authUtil;
	private final MemberRepository memberRepository;
	private final S3Uploader s3Uploader;
	private final MemberStoryRedisRepository memberStoryRedisRepository;
	private final PostRepository postRepository;
	private final PostImageRepository postImageRepository;

	@Transactional(readOnly = true)
	public MenuMemberDTO getMenuMemberProfile() {
		final Member member = authUtil.getLoginMember();

		return MenuMemberDTO.builder()
			.memberId(member.getId())
			.memberUsername(member.getUsername())
			.memberName(member.getName())
			.memberImageUrl(member.getImage().getImageUrl())
			.build();
	}

	@Transactional(readOnly = true)
	public UserProfileResponse getUserProfile(String username) {
		final Long memberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		UserProfileResponse result = memberRepository.getUserProfile(memberId, member.getUsername());
		result.setHasStory(memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0);

		return result;
	}

	@Transactional(readOnly = true)
	public MiniProfileResponse getMiniProfile(String username) {
		final Long memberId = authUtil.getLoginMemberId();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		MiniProfileResponse result = memberRepository.getMiniProfile(memberId, member.getUsername());
		result.setHasStory(memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0);
		setMemberPostImages(result, member.getId());
		return result;
	}

	private void setMemberPostImages(MiniProfileResponse miniProfileResponse, Long memberId) {
		final List<Post> posts = postRepository.findTop3ByMemberIdOrderByIdDesc(memberId);
		final List<Long> postIds = posts.stream()
			.map(Post::getId)
			.collect(Collectors.toList());
		final List<PostImageDTO> postImages = postImageRepository.findAllPostImageDto(postIds);

		final Map<Long, List<PostImageDTO>> postDTOMap = postImages.stream()
			.collect(Collectors.groupingBy(PostImageDTO::getPostId));

		List<MiniProfilePostDTO> results = new ArrayList<>();
		postDTOMap.forEach((id, p) -> results.add(
			MiniProfilePostDTO.builder()
				.postId(id)
				.postImageUrl(p.get(0).getPostImageUrl())
				.build()));

		miniProfileResponse.setMemberPosts(results);
	}

	@Transactional
	public void uploadMemberImage(MultipartFile uploadedImage) {
		Member member = authUtil.getLoginMember();

		// 기존 사진 삭제
		Image originalImage = member.getImage();
		s3Uploader.deleteImage("member", originalImage);

		Image image = s3Uploader.uploadImage(uploadedImage, "member");
		member.uploadImage(image);
		memberRepository.save(member);
	}

	@Transactional
	public void deleteMemberImage() {
		Member member = authUtil.getLoginMember();
		Image image = member.getImage();
		s3Uploader.deleteImage("member", image);
		member.deleteImage();
		memberRepository.save(member);
	}

	public EditProfileResponse getEditProfile() {
		Member member = authUtil.getLoginMember();
		return EditProfileResponse.builder()
			.memberUsername(member.getUsername())
			.memberName(member.getName())
			.memberImageUrl(member.getImage().getImageUrl())
			.memberGender(member.getGender().toString())
			.memberEmail(member.getEmail())
			.memberIntroduce(member.getIntroduce())
			.memberWebsite(member.getWebsite())
			.memberPhone(member.getPhone())
			.build();
	}

	// TODO 변경시 이메일 인증 로직은?
	public void editProfile(EditProfileRequest editProfileRequest) {
		Member member = authUtil.getLoginMember();

		if (memberRepository.existsByUsername(editProfileRequest.getMemberUsername())
			&& !member.getUsername().equals(editProfileRequest.getMemberUsername())) {
			throw new UsernameAlreadyExistException();
		}

		member.updateUsername(editProfileRequest.getMemberUsername());
		member.updateName(editProfileRequest.getMemberName());
		member.updateEmail(editProfileRequest.getMemberEmail());
		member.updateIntroduce(editProfileRequest.getMemberIntroduce());
		member.updateWebsite(editProfileRequest.getMemberWebsite());
		member.updatePhone(editProfileRequest.getMemberPhone());
		member.updateGender(Gender.valueOf(editProfileRequest.getMemberGender()));
		memberRepository.save(member);
	}
}
