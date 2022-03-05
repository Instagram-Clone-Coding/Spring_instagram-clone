package cloneproject.Instagram.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import cloneproject.Instagram.advice.GlobalExceptionHandler;
import cloneproject.Instagram.dto.member.JwtDto;
import cloneproject.Instagram.dto.member.LoginRequest;
import cloneproject.Instagram.dto.member.RegisterRequest;
import cloneproject.Instagram.dto.member.SendConfirmationEmailRequest;
import cloneproject.Instagram.dto.member.UpdatePasswordRequest;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.MemberAuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;

// TODO 비밀번호 리셋 추가
@ExtendWith(SpringExtension.class)
@DisplayName("Member Auth Controller Test")
public class MemberAuthControllerTest {
    
    @InjectMocks
    private MemberAuthController memberAuthController;

    @Mock
    private MemberAuthService memberAuthService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


	@BeforeEach
	private void setup() {
        objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(memberAuthController)
			.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
            .setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

    
    @Nested
    @DisplayName("username 중복 조회는")
    class Describe_checkUsername{

        @Nested
        @DisplayName("올바른 parameter가 주어지면")
        class Context_correct_parameter{
            @Test
            @DisplayName("처리 결과를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(get("/accounts/check")
                        .param("username", "dlwlrma")
                    )
                    .andExpect(status().isOk())
                    // Mocking된 MemberAuthService는 0을 반환하기 때문에 false가 반환됨.
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.CHECK_USERNAME_BAD, false))));
            }  
        }

        // TODO Postman, swagger에선 에러가 정상적으로 반환되나 Test시엔 200이 반환됨.
        // @Nested
        // @DisplayName("잘못된 parameter가 주어지면")
        // class Context_wrong_parameter{

        //     @Test
        //     @DisplayName("400에러가 발생한다")
        //     void it_return_error() throws Exception{
        //         mockMvc.perform(get("/accounts/check")
        //                     .param("username", "aa")
        //                 )
        //                 .andExpect(status().isBadRequest());
        //     }  
            
        // }
    }
    
    @Nested
    @DisplayName("회원 가입은")
    class Describe_register{

        @Nested
        @DisplayName("올바른 parameters가 주어지면")
        class Context_correct_params{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                RegisterRequest registerRequest = new RegisterRequest("dlwlrma", "이지금", "a12341234", "aaa@gmail.com", "ABC123");
                when(memberAuthService.register(any())).thenReturn(true);

                mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.REGISTER_SUCCESS, true))));
            }
        }

        @Nested
        @DisplayName("잘못된 parameters가 주어지면")
        class Context_wrong_params{
            @Test
            @DisplayName("400 에러가 발생한다")
            void it_return_error() throws Exception{
                RegisterRequest registerRequest = new RegisterRequest("dlwlrma", "이지금", "a123", "aaa@gmail.com", "ABC123");

                mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest))
                    )
                    .andExpect(status().isBadRequest());
            }
        }

    }

    @Nested
    @DisplayName("이메일코드 전송은")
    class Describe_sendConfirmEmail{

        @Nested
        @DisplayName("올바른 parameters가 주어지면")
        class Context_correct_params{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                SendConfirmationEmailRequest sendConfirmationEmailRequest = 
                    new SendConfirmationEmailRequest("dlwlrma", "aaa@gmail.com");

                mockMvc.perform(post("/accounts/email")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sendConfirmationEmailRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.SEND_CONFIRM_EMAIL_SUCCESS, null))));
            }
        }

        @Nested
        @DisplayName("잘못된 parameters가 주어지면")
        class Context_wrong_params{
            @Test
            @DisplayName("400 에러가 발생한다")
            void it_return_error() throws Exception{
                SendConfirmationEmailRequest sendConfirmationEmailRequest = 
                new SendConfirmationEmailRequest("dlwlrma", "gmail.com");

                mockMvc.perform(post("/accounts/email")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sendConfirmationEmailRequest))
                    )
                    .andExpect(status().isBadRequest());
            }
        }

    }

    @Nested
    @DisplayName("login은")
    class Describe_login{

        @Nested
        @DisplayName("올바른 파라미터가 주어지면")
        class Context_correct_parameter{

            @Test
            @DisplayName("쿠키와 JSON를 통해 JWT토큰을 반환한다")
            void it_returns_using_cookie_and_json() throws Exception{
                LoginRequest loginRequest = new LoginRequest("dlwlrma", "a12341234");
                JwtDto jwtDto = JwtDto.builder()
                    .type("Bearer")
                    .accessToken("AAA.BBB.CCC")
                    .refreshToken("CCC.BBB.AAA")
                    .build();
                when(memberAuthService.login(any())).thenReturn(jwtDto);

                mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                    )
                    .andExpect(cookie().value("refreshToken", jwtDto.getRefreshToken()))
                    .andExpect(status().isOk());
            }

        }
        
    }

    
    @Nested
    @DisplayName("reissue는")
    class Describe_reissue{

        @Nested
        @DisplayName("올바른 파라미터가 주어지면")
        class Context_correct_parameter{

            @Test
            @DisplayName("쿠키와 JSON를 통해 JWT토큰을 반환한다")
            void it_returns_using_cookie_and_json() throws Exception{
                JwtDto jwtDto = JwtDto.builder()
                    .type("Bearer")
                    .accessToken("AAA.BBB.CCC")
                    .refreshToken("CCC.BBB.AAA")
                    .build();
                when(memberAuthService.reisuue("CCC.BBB.AAA")).thenReturn(jwtDto);

                mockMvc.perform(post("/reissue")
                        .cookie(new Cookie("refreshToken", "CCC.BBB.AAA"))
                    )
                    .andExpect(cookie().value("refreshToken", jwtDto.getRefreshToken()))
                    .andExpect(status().isOk());
            }

        }
        
    }
    
    @Nested
    @DisplayName("비밀번호 변경은")
    class Describe_updatePassword{

        @Nested
        @DisplayName("올바른 parameters가 주어지면")
        class Context_correct_params{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                UpdatePasswordRequest updatePasswordRequest = 
                    new UpdatePasswordRequest("a12341234", "b12341234");

                mockMvc.perform(put("/accounts/password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatePasswordRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.UPDATE_PASSWORD_SUCCESS, null))));
            }
        }

        @Nested
        @DisplayName("잘못된 parameters가 주어지면")
        class Context_wrong_params{
            @Test
            @DisplayName("400 에러가 발생한다")
            void it_return_error() throws Exception{
                UpdatePasswordRequest updatePasswordRequest = 
                    new UpdatePasswordRequest("a12341234", "b1234");

                    mockMvc.perform(put("/accounts/password")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updatePasswordRequest))
                        )
                        .andExpect(status().isBadRequest());
            }
        }

    }

}
