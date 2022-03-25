package cloneproject.Instagram.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cloneproject.Instagram.WithMockCustomUser;
import cloneproject.Instagram.domain.member.dto.EditProfileRequest;
import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.exception.UseridAlreadyExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.repository.redis.EmailCodeRedisRepository;
import cloneproject.Instagram.domain.member.service.MemberService;
import cloneproject.Instagram.global.util.ImageUtil;

@ExtendWith(SpringExtension.class)
@DisplayName("Member Service")
public class MemberServiceTest {
    
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailCodeRedisRepository emailCodeRedisRepository;

    @BeforeEach
    void setRepostiory(){
        Member member = Member.builder()
                            .username("dlwlrma")
                            .name("이지금")
                            .email("aaa@gmail.com")
                            .password("1234")
                            .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.findByUsername("dlwlrma")).thenReturn(Optional.of(member));

    }
    @Nested
    @DisplayName("유저 상단 프로필 조회 API는")
    class Describe_menu_member_profile_api{
        
        @Nested
        @DisplayName("로그인 한 경우에는")
        class Context_logined{
            @WithMockCustomUser
            @DisplayName("정상적으로 조회된다")
            @Test
            void it_load_well(){
                assertEquals("dlwlrma", memberService.getMenuMemberProfile().getMemberUsername(), "이름이 같나");
            }
        }
        @Nested
        @DisplayName("로그인 하지 않은 경우에는")
        class Context_unlogined{
            @DisplayName("에러가 발생한다")
            @Test
            void it_occur_exception(){
                // 실제 API에선 filter에 의해 NoAuthorityException 발생
                // 실제론 service 진입전에 인증 검증을 하기 때문에 service에서 사용할 땐 널포인터가 발생
                assertThrows(NullPointerException.class, ()->memberService.getMenuMemberProfile());
            }
        }
    }

    @Nested
    @DisplayName("유저 프로필 조회 API는")
    class Describe_member_profile_api{
        
        @Nested
        @DisplayName("올바른 username이 주어지면")
        class Context_correct_username{

            @WithMockCustomUser
            @DisplayName("정상적으로 조회된다")
            @Test
            void it_load_well(){
                when(memberRepository.getUserProfile(1L, "dlwlrma")).thenReturn(
                    new UserProfileResponse("dlwlrma", "이지금", "", ImageUtil.getBaseImage(),
                    true, false, false, false, "이지금 입니다", 2L, 1L, 1L, true)
                );
                assertEquals("dlwlrma", memberService.getUserProfile("dlwlrma").getMemberUsername(), "이름이 같나");
            }
        }

        @Nested
        @DisplayName("올바르지 않은 username이 주어지면")
        class Context_uncorrect_username{

            @WithMockCustomUser
            @DisplayName("에러가 발생한다")
            @Test
            void it_occur_exception(){
                assertThrows(MemberDoesNotExistException.class, ()->memberService.getUserProfile("dlwlrma1"));
            }
        }
    }


    @Nested
    @DisplayName("유저 미니 프로필 조회 API는")
    class Describe_mini_profile_api{
        
        @Nested
        @DisplayName("올바른 username이 주어지면")
        class Context_correct_username{

            @WithMockCustomUser
            @DisplayName("정상적으로 조회된다")
            @Test
            void it_load_well(){
                when(memberRepository.getMiniProfile(1L, "dlwlrma")).thenReturn(
                    new MiniProfileResponse("dlwlrma", "이지금", ImageUtil.getBaseImage(),
                    true, false, false, false, 2L, 1L, 1L, true)
                );
                assertEquals("dlwlrma", memberService.getMiniProfile("dlwlrma").getMemberUsername(), "이름이 같나");
            }
        }

        @Nested
        @DisplayName("올바르지 않은 username이 주어지면")
        class Context_uncorrect_username{

            @WithMockCustomUser
            @DisplayName("에러가 발생한다")
            @Test
            void it_occur_exception(){
                assertThrows(MemberDoesNotExistException.class, ()->memberService.getMiniProfile("dlwlrma1"));
            }
        }
    }

    @Nested
    @DisplayName("유저 프로필 수정 조회 API는")
    class Describe_get_edit_profile{
        
        @Nested
        @DisplayName("로그인 한 경우에는")
        class Context_logined{
            @WithMockCustomUser
            @DisplayName("정상적으로 조회된다")
            @Test
            void it_load_well(){
                assertEquals("dlwlrma", memberService.getMenuMemberProfile().getMemberUsername(), "이름이 같나");
            }
        }
        @Nested
        @DisplayName("로그인 하지 않은 경우에는")
        class Context_unlogined{
            @DisplayName("에러가 발생한다")
            @Test
            void it_occur_exception(){
                // 실제 API에선 filter에 의해 NoAuthorityException 발생
                // 실제론 service 진입전에 인증 검증을 하기 때문에 service에서 사용할 땐 널포인터가 발생
                assertThrows(NullPointerException.class, ()->memberService.getEditProfile());
            }
        }
    }

    @Nested
    @DisplayName("유저 프로필 수정 API는")
    class Describe_edit_profile{
        
        @Nested
        @DisplayName("로그인 한 경우에는")
        class Context_logined{
            @WithMockCustomUser
            @DisplayName("정상적으로 수정된다")
            @Test
            void it_load_well(){
                when(memberRepository.existsByUsername("dlwlrma")).thenReturn(false);
                EditProfileRequest editProfileRequest = new EditProfileRequest("dlwlrma", "이지금", "", "", "aaa@gmail.com", "", "FEMALE");
                memberService.editProfile(editProfileRequest);
                verify(memberRepository).findById(1L);
            }
        }

        @Nested
        @DisplayName("로그인 하지 않은 경우에는")
        class Context_unlogined{
            @DisplayName("에러가 발생한다")
            @Test
            void it_occur_exception(){
                EditProfileRequest editProfileRequest = new EditProfileRequest("dlwlrma", "이지금", "", "", "aaa@gmail.com", "", "FEMALE");
                assertThrows(NullPointerException.class, ()->memberService.editProfile(editProfileRequest));
            }
        }

        @Nested
        @DisplayName("유저네임이 중복된 경우에는")
        class Context_duplicated_username{
            @DisplayName("에러가 발생한다")
            @Test
            @WithMockCustomUser
            void it_occur_exception(){
                when(memberRepository.existsByUsername("dlwlrma1")).thenReturn(true);
                EditProfileRequest editProfileRequest = new EditProfileRequest("dlwlrma1", "이지금", "", "", "aaa@gmail.com", "", "FEMALE");
                assertThrows(UseridAlreadyExistException.class, ()->memberService.editProfile(editProfileRequest));
            }
        }

    }

    // TODO 프로필사진 UPLOAD/DELETE 는 어떻게?

}
