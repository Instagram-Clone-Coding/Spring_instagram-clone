package cloneproject.Instagram.controller;

import cloneproject.Instagram.dto.chat.ChatRoomCreateResponse;
import cloneproject.Instagram.dto.chat.ChatRoomInquireResponse;
import cloneproject.Instagram.dto.chat.JoinRoomDTO;
import cloneproject.Instagram.dto.chat.JoinRoomDeleteResponse;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static cloneproject.Instagram.dto.result.ResultCode.*;

@Api(tags = "채팅 API")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @ApiOperation(value = "채팅방 생성")
    @ApiImplicitParam(name = "username", value = "상대방 username", example = "dlwlrma1", required = true)
    @PostMapping("/chat/rooms")
    public ResponseEntity<ResultResponse> createChatRoom(
            @Validated @NotBlank(message = "상대방 username은 필수입니다.") @RequestParam String username) {
        final ChatRoomCreateResponse response = chatService.createRoom(username);

        return ResponseEntity.ok(ResultResponse.of(CREATE_CHAT_ROOM_SUCCESS, response));
    }

    @ApiOperation(value = "채팅방 조회")
    @DeleteMapping("/chat/rooms/{roomId}")
    public ResponseEntity<ResultResponse> inquireChatRoom(@PathVariable Long roomId) {
        final ChatRoomInquireResponse response = chatService.inquireRoom(roomId);

        return ResponseEntity.ok(ResultResponse.of(INQUIRE_CHAT_ROOM_SUCCESS, response));
    }

    @ApiOperation(value = "채팅방 삭제")
    @DeleteMapping("/chat/rooms/hide")
    public ResponseEntity<ResultResponse> hideChatRoom(
            @Validated @NotNull(message = "채팅방 PK는 필수입니다.") @RequestParam Long roomId) {
        final JoinRoomDeleteResponse response = chatService.deleteJoinRoom(roomId);

        return ResponseEntity.ok(ResultResponse.of(DELETE_JOIN_ROOM_SUCCESS, response));
    }

    @ApiOperation(value = "채팅방 목록 페이징 조회", notes = "1페이지당 10개씩 조회할 수 있습니다.")
    @GetMapping("/chat/rooms")
    public ResponseEntity<ResultResponse> getJoinRooms(
            @Validated @NotNull(message = "페이지는 필수입니다.") @RequestParam Integer page) {
        final Page<JoinRoomDTO> response = chatService.getJoinRooms(page);

        return ResponseEntity.ok(ResultResponse.of(GET_JOIN_ROOMS_SUCCESS, response));
    }
}
