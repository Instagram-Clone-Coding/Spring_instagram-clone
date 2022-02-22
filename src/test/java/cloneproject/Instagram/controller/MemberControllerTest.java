package cloneproject.Instagram.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import cloneproject.Instagram.advice.GlobalExceptionHandler;
import cloneproject.Instagram.dto.member.EditProfileRequest;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.MemberService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Collections;


import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@DisplayName("Member Controller Test")
public class MemberControllerTest {
    
    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberService memberService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


	@BeforeEach
	private void setup() {
        objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(memberController)
			.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
	}

    @Nested
    @DisplayName("메뉴 유저 프로필은")
    class Describe_getMenuMemberProfile{

        @Nested
        @DisplayName("로그인한 상태라면")
        class Context_logined{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(get("/menu/profile"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.GET_MENU_MEMBER_SUCCESS, null))));
            }  
        }

    }
    
    @Nested
    @DisplayName("유저 프로필은")
    class Describe_getMemberProfile{

        @Nested
        @DisplayName("올바른 username이 주어지면")
        class Context_correct_username{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(get("/accounts/dlwlrma"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.GET_USERPROFILE_SUCCESS, null))));
            }
        }

    }

    @Nested
    @DisplayName("미니 유저 프로필은")
    class Describe_getMiniProfile{
        @Nested
        @DisplayName("올바른 username이 주어지면")
        class Context_correct_username{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(get("/accounts/dlwlrma/mini"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.GET_MINIPROFILE_SUCCESS, null))));
            }
        }
    }

    @Nested
    @DisplayName("이미지 업로드는")
    class Describe_uploadImage{
        @Nested
        @DisplayName("올바른 파일이 주어지면")
        class Context_correct_file{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(multipart("/accounts/image").file(
                        new MockMultipartFile("uploadedImage","hello.png", "png"," ".getBytes()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.UPLOAD_MEMBER_IMAGE_SUCCESS, null))));
            }
        }
        @Nested
        @DisplayName("파일이 없으면")
        class Context_no_file{
            @Test
            @DisplayName("400 에러가 발생한다")
            void it_return_error() throws Exception{
                mockMvc.perform(multipart("/accounts/image"))
                    .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("이미지 삭제는")
    class Describe_deleteImage{
        @Nested
        @DisplayName("로그인한 상태라면")
        class Context_logined{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(delete("/accounts/image"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.DELETE_MEMBER_IMAGE_SUCCESS, null))));
            }
        }
    }
    

    @Nested
    @DisplayName("회원 수정정보 조회는")
    class Describe_getEditProfile{
        @Nested
        @DisplayName("로그인한 상태라면")
        class Context_logined{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(get("/accounts/edit"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.GET_EDIT_PROFILE_SUCCESS, null))));
            }
        }
    }

    @Nested
    @DisplayName("회원 수정은")
    class Describe_editProfile{
        @Nested
        @DisplayName("올바른 parameters가 주어지면")
        class Context_correct_params{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                EditProfileRequest editProfileRequest = new EditProfileRequest("dlwlrma", "이지금", null, null, "aaa@gmail.com", null, "PRIVATE");
                mockMvc.perform(put("/accounts/edit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(editProfileRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.EDIT_PROFILE_SUCCESS, null))));
            }
        }

        @Nested
        @DisplayName("잘못된 parameters가 주어지면")
        class Context_wrong_params{
            @Test
            @DisplayName("400 에러가 발생한다")
            void it_return_error() throws Exception{
                EditProfileRequest editProfileRequest = new EditProfileRequest("dlw", "이지금", null, null, "aaa@gmail.com", null, "PRIVATE");
                mockMvc.perform(put("/accounts/edit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(editProfileRequest))
                    )
                    .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("회원 검색은")
    class Describe_searchMember{
        @Nested
        @DisplayName("parameter가 주어지면")
        class Context_given_param{
            @Test
            @DisplayName("성공 ResultCode를 반환한다")
            void it_return_success() throws Exception{
                mockMvc.perform(get("/search").param("text", "dlwl"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.SEARCH_MEMBER_SUCCESS, Collections.emptyList()))));
            }
        }

        @Nested
        @DisplayName("parameter가 주어지지 않으면")
        class Context_not_given_param{
            @Test
            @DisplayName("400 에러를 반환한다")
            void it_return_error() throws Exception{
                mockMvc.perform(get("/search"))
                    .andExpect(status().isBadRequest());
            }
        }
    }

}
