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
import cloneproject.Instagram.service.BlockService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DisplayName("Block Controller Test")
public class BlockControllerTest {
    
    @InjectMocks
    private BlockController blockController;

    @Mock
    private BlockService blockService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


	@BeforeEach
	private void setup() {
        objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(blockController)
			.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
	}

    @Nested
    @DisplayName("block은")
    class Describe_block은{

        @Nested
        @DisplayName("올바른 parameter가 주어지면")
        class Context_correct_parameter{
            @Test
            @DisplayName("처리 결과를 반환한다")
            void it_return_success() throws Exception{
                when(blockService.block(any())).thenReturn(true);
                mockMvc.perform(post("/{followMemberUsername}/block", "dlwlrma1")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.BLOCK_SUCCESS, true))));
            }  
        }

    }

    @Nested
    @DisplayName("unblock은는")
    class Describe_unblock은{

        @Nested
        @DisplayName("올바른 parameter가 주어지면")
        class Context_correct_parameter{
            @Test
            @DisplayName("처리 결과를 반환한다")
            void it_return_success() throws Exception{
                when(blockService.unblock(any())).thenReturn(true);
                mockMvc.perform(delete("/{followMemberUsername}/block", "dlwlrma1")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(
                        ResultResponse.of(ResultCode.UNBLOCK_SUCCESS, true))));
            }
        }
    }
}
