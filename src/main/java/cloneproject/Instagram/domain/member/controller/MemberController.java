package cloneproject.Instagram.domain.member.controller;

import static cloneproject.Instagram.global.result.ResultCode.*;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.domain.member.dto.EditProfileRequest;
import cloneproject.Instagram.domain.member.dto.EditProfileResponse;
import cloneproject.Instagram.domain.member.dto.MenuMemberProfile;
import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.service.MemberService;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = "멤버 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class MemberController {

	private final MemberService memberService;

	@ApiOperation(value = "상단 메뉴 로그인한 유저 프로필 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M016 - 상단 메뉴 프로필을 조회하였습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@GetMapping(value = "/profile")
	public ResponseEntity<ResultResponse> getMenuMemberProfile() {
		final MenuMemberProfile menuMemberProfile = memberService.getMenuMemberProfile();

		return ResponseEntity.ok(ResultResponse.of(GET_MENU_MEMBER_SUCCESS, menuMemberProfile));
	}

	@ApiOperation(value = "유저 프로필 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M004 - 회원 프로필을 조회하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	@GetMapping(value = "/{username}")
	public ResponseEntity<ResultResponse> getUserProfile(@PathVariable("username") String username) {
		final UserProfileResponse userProfileResponse = memberService.getUserProfile(username);

		return ResponseEntity.ok(ResultResponse.of(GET_USERPROFILE_SUCCESS, userProfileResponse));
	}

	@ApiOperation(value = "미니 프로필 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M005 - 미니 프로필을 조회하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	@GetMapping(value = "/{username}/mini")
	public ResponseEntity<ResultResponse> getMiniProfile(@PathVariable("username") String username) {
		final MiniProfileResponse miniProfileResponse = memberService.getMiniProfile(username);

		return ResponseEntity.ok(ResultResponse.of(GET_MINIPROFILE_SUCCESS, miniProfileResponse));
	}

	@ApiOperation(value = "회원 프로필 사진 업로드")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M006 - 회원 이미지를 등록하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "G007 - 지원하지 않는 이미지 타입입니다.\n"
			+ "G008 - 변환할 수 없는 파일입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@PostMapping(value = "/image")
	public ResponseEntity<ResultResponse> uploadImage(@RequestParam MultipartFile uploadedImage) {
		memberService.uploadMemberImage(uploadedImage);

		return ResponseEntity.ok(ResultResponse.of(UPLOAD_MEMBER_IMAGE_SUCCESS));
	}

	@ApiOperation(value = "회원 프로필 사진 삭제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M007 - 회원 이미지를 삭제하였습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@DeleteMapping(value = "/image")
	public ResponseEntity<ResultResponse> deleteImage() {
		memberService.deleteMemberImage();

		return ResponseEntity.ok(ResultResponse.of(DELETE_MEMBER_IMAGE_SUCCESS));
	}

	@ApiOperation(value = "회원 프로필 수정정보 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M008 - 회원 프로필 수정정보를 조회하였습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@GetMapping(value = "/edit")
	public ResponseEntity<ResultResponse> getMemberEdit() {
		final EditProfileResponse editProfileResponse = memberService.getEditProfile();

		return ResponseEntity.ok(ResultResponse.of(GET_EDIT_PROFILE_SUCCESS, editProfileResponse));
	}

	@ApiOperation(value = "회원 프로필 수정")
	@ApiResponses({
		@ApiResponse(code = 200, message = "M009 - 회원 프로필을 수정하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@PutMapping(value = "/edit")
	public ResponseEntity<ResultResponse> editProfile(@Valid @RequestBody EditProfileRequest editProfileRequest) {
		memberService.editProfile(editProfileRequest);

		return ResponseEntity.ok(ResultResponse.of(EDIT_PROFILE_SUCCESS));
	}

}
