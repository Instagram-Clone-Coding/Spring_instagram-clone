package cloneproject.Instagram.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cloneproject.Instagram.domain.member.dto.JwtDto;
import cloneproject.Instagram.domain.member.dto.LoginRequest;
import cloneproject.Instagram.domain.member.dto.RegisterRequest;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.UseridAlreadyExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.service.EmailCodeService;
import cloneproject.Instagram.domain.member.service.MemberAuthService;
import cloneproject.Instagram.global.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DisplayName("Follow Service")
public class MemberAuthServiceTest {

    @InjectMocks
    private MemberAuthService memberAuthService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailCodeService emailCodeService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
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
    @DisplayName("username 중복조회 API는")
    class Describe_checkUsername{
        
        @Nested
        @DisplayName("존재하는 경우에는")
        class Context_exist{
            @DisplayName("false를 반환한다")
            @Test
            void it_return_false(){
                when(memberRepository.existsByUsername("dlwlrma")).thenReturn(true);

                assertEquals(false, memberAuthService.checkUsername("dlwlrma"));
            }
        }
        @Nested
        @DisplayName("존재하지 않는 경우에는")
        class Context_dont_exist{
            @DisplayName("true를 반환한다")
            @Test
            void it_return_true(){
                assertEquals(true, memberAuthService.checkUsername("dlwlrma1"));
            }
        }
    }

    @Nested
    @DisplayName("register는")
    class Describe_register{
        
        @Nested
        @DisplayName("username이 존재하는 경우에는")
        class Context_username_already_exist{
            @DisplayName("exception을 발생시킨다")
            @Test
            void it_throw_exception(){
                RegisterRequest registerRequest = new RegisterRequest("dlwlrma", "이지금", "a12341234", "aaa@gmail.com", "ABC123");
                when(memberRepository.existsByUsername("dlwlrma")).thenReturn(true);

                assertThrows(UseridAlreadyExistException.class, ()->memberAuthService.register(registerRequest));
            }
        }
        
        @Nested
        @DisplayName("EmailCode가 올바르지 않은 경우에는")
        class Context_dont_exist{
            @DisplayName("false를 반환한다")
            @Test
            void it_return_false(){
                RegisterRequest registerRequest = new RegisterRequest("dlwlrma", "이지금", "a12341234", "aaa@gmail.com", "ABC123");
                assertEquals(false, memberAuthService.register(registerRequest));
            }
        }
        
        @Nested
        @DisplayName("정상적인 경우에는")
        class Context_correct_process{
            @DisplayName("true를 반환한다")
            @Test
            void it_return_true(){
                RegisterRequest registerRequest = new RegisterRequest("dlwlrma", "이지금", "a12341234", "aaa@gmail.com", "ABC123");
                when(emailCodeService.checkEmailCode(any(), any(), any())).thenReturn(true);

                assertEquals(true, memberAuthService.register(registerRequest));
            }
        }
    }

    @Nested
    @DisplayName("sendEmailConfirmation는")
    class Describe_sendEmailConfirmation{
        
        @Nested
        @DisplayName("username이 존재하는 경우에는")
        class Context_username_exist{
            @DisplayName("exception을 발생시킨다")
            @Test
            void it_throw_exception(){
                when(memberRepository.existsByUsername("dlwlrma")).thenReturn(true);

                assertThrows(UseridAlreadyExistException.class, ()->memberAuthService.sendEmailConfirmation("dlwlrma", ""));
            }
        }
        @Nested
        @DisplayName("username이 존재하지 않는 경우에는")
        class Context_username_dont_exist{
            @DisplayName("정상적으로 실행된다")
            @Test
            void it_do_well(){
                memberAuthService.sendEmailConfirmation("dlwlrma", "");
                verify(emailCodeService, times(1)).sendEmailConfirmationCode(any(), any());
            }
        }
    }
    
    // TODO AuthenticationManagerBuilder NullPointerException 해결
    // @Nested
    // @DisplayName("login은")
    // class Describe_login{
        
    //     @Nested
    //     @DisplayName("올바른 정보를 입력하면")
    //     class Context_correct_account{
    //         @DisplayName("JWT 토큰이 반환된다")
    //         @Test
    //         void it_return_jwt() throws Exception{
    //             LoginRequest loginRequest = new LoginRequest("dlwlrma", "a12341234");
    //             JwtDto jwtDto = JwtDto.builder()
    //                 .type("Bearer")
    //                 .accessToken("AAA.BBB.CCC")
    //                 .refreshToken("CCC.BBB.AAA")
    //                 .build();
    //             when(jwtUtil.generateTokenDto(any())).thenReturn(jwtDto);

    //             assertEquals(jwtDto, memberAuthService.login(loginRequest));
    //         }
    //     }
        
    //     @Nested
    //     @DisplayName("EmailCode가 올바르지 않은 경우에는")
    //     class Context_dont_exist{
    //         @DisplayName("false를 반환한다")
    //         @Test
    //         void it_return_false(){
    //             RegisterRequest registerRequest = new RegisterRequest("dlwlrma", "이지금", "a12341234", "aaa@gmail.com", "ABC123");
    //             assertEquals(false, memberAuthService.register(registerRequest));
    //         }
    //     }
        
    //     @Nested
    //     @DisplayName("정상적인 경우에는")
    //     class Context_correct_process{
    //         @DisplayName("true를 반환한다")
    //         @Test
    //         void it_return_true(){
    //             RegisterRequest registerRequest = new RegisterRequest("dlwlrma", "이지금", "a12341234", "aaa@gmail.com", "ABC123");
    //             when(emailCodeService.checkEmailCode(any(), any(), any())).thenReturn(true);

    //             assertEquals(true, memberAuthService.register(registerRequest));
    //         }
    //     }
    // }

}
