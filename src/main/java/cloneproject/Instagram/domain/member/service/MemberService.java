package cloneproject.Instagram.domain.member.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.domain.member.dto.EditProfileRequest;
import cloneproject.Instagram.domain.member.dto.EditProfileResponse;
import cloneproject.Instagram.domain.member.dto.MenuMemberDTO;
import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.entity.Gender;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.exception.UsernameAlreadyExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.infra.aws.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final AuthUtil authUtil;
	private final MemberRepository memberRepository;
	private final S3Uploader s3Uploader;

	@Transactional(readOnly = true)
	public MenuMemberDTO getMenuMemberProfile() {
		final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

		final Member member = memberRepository.findById(Long.valueOf(memberId))
				.orElseThrow(MemberDoesNotExistException::new);

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
				.orElseThrow(MemberDoesNotExistException::new);

		UserProfileResponse result = memberRepository.getUserProfile(memberId, member.getUsername());

		return result;
	}

	@Transactional(readOnly = true)
	public MiniProfileResponse getMiniProfile(String username) {
		final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

		final Member member = memberRepository.findByUsername(username)
				.orElseThrow(MemberDoesNotExistException::new);

		MiniProfileResponse result = memberRepository.getMiniProfile(Long.valueOf(memberId), member.getUsername());

		return result;
	}

	@Transactional
	public void uploadMemberImage(MultipartFile uploadedImage) {
		final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
		Member member = memberRepository.findById(Long.valueOf(memberId))
				.orElseThrow(MemberDoesNotExistException::new);

		// 기존 사진 삭제
		Image originalImage = member.getImage();
		s3Uploader.deleteImage("member", originalImage);

		Image image = s3Uploader.uploadImage(uploadedImage, "member");
		member.uploadImage(image);
		memberRepository.save(member);
	}

	@Transactional
	public void deleteMemberImage() {
		final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
		Member member = memberRepository.findById(Long.valueOf(memberId))
				.orElseThrow(MemberDoesNotExistException::new);
		Image image = member.getImage();
		s3Uploader.deleteImage("member", image);
		member.deleteImage();
		memberRepository.save(member);
	}

	public EditProfileResponse getEditProfile() {
		final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
		Member member = memberRepository.findById(Long.valueOf(memberId))
				.orElseThrow(MemberDoesNotExistException::new);
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
		final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
		Member member = memberRepository.findById(Long.valueOf(memberId))
				.orElseThrow(MemberDoesNotExistException::new);

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
