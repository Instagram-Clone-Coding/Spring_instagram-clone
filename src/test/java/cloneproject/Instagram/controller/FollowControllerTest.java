package cloneproject.Instagram.controller;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import cloneproject.Instagram.advice.GlobalExceptionHandler;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.FollowService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DisplayName("Follow Controller Test")
public class FollowControllerTest {
    
    @InjectMocks
    private FollowController followController;

    @Mock
    private FollowService followService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


	@BeforeEach
	private void setup() {
        objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(followController)
			.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
	}

    @Nested
    @DisplayName("follow는")
    class Describe_follow{

        @Nested
        @DisplayName("올바른 parameter가 주어지면")
        class Context_correct_parameter{
            @Test
            @DisplayName("처리 결과를 반환한다")
            void it_return_success() throws Exception{
                when(followService.follow(any())).thenReturn(true);
                mockMvc.perform(post("/{followMemberUsername}/follow", "dlwlrma1")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.FOLLOW_SUCCESS, true))));
            }  
        }

    }

    @Nested
    @DisplayName("unfollow는")
    class Describe_unfollow{

        @Nested
        @DisplayName("올바른 parameter가 주어지면")
        class Context_correct_parameter{
            @Test
            @DisplayName("처리 결과를 반환한다")
            void it_return_success() throws Exception{
                when(followService.unfollow(any())).thenReturn(true);
                mockMvc.perform(delete("/{followMemberUsername}/follow", "dlwlrma1")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.UNFOLLOW_SUCCESS, true))));
            }
        }
    }

    @Nested
    @DisplayName("getFollowings는")
    class Describe_getFollowings{

        @Nested
        @DisplayName("올바른 parameter가 주어지면")
        class Context_correct_parameter{
            @Test
            @DisplayName("처리 결과를 반환한다")
            void it_return_success() throws Exception{
                when(followService.getFollowings(any())).thenReturn(null);
                mockMvc.perform(get("/{followMemberUsername}/following", "dlwlrma1")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.GET_FOLLOWINGS_SUCCESS, null))));
            }
        }
    }

    @Nested
    @DisplayName("getFollowers는")
    class Describe_getFollowers{

        @Nested
        @DisplayName("올바른 parameter가 주어지면")
        class Context_correct_parameter{
            @Test
            @DisplayName("처리 결과를 반환한다")
            void it_return_success() throws Exception{
                when(followService.getFollowers(any())).thenReturn(null);
                mockMvc.perform(get("/{followMemberUsername}/followers", "dlwlrma1")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.GET_FOLLOWERS_SUCCESS, null))));
            }
        }
    }

}
