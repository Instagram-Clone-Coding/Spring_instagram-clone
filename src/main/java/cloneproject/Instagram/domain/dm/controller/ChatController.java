package cloneproject.Instagram.domain.dm.controller;

import cloneproject.Instagram.domain.dm.dto.ChatRoomCreateResponse;
import cloneproject.Instagram.domain.dm.dto.ChatRoomInquireResponse;
import cloneproject.Instagram.domain.dm.dto.IndicateRequest;
import cloneproject.Instagram.domain.dm.dto.JoinRoomDto;
import cloneproject.Instagram.domain.dm.dto.JoinRoomDeleteResponse;
import cloneproject.Instagram.domain.dm.dto.MessageDto;
import cloneproject.Instagram.domain.dm.dto.MessageRequest;
import cloneproject.Instagram.domain.dm.dto.MessageSimpleRequest;
import cloneproject.Instagram.domain.dm.dto.SignalRequest;
import cloneproject.Instagram.domain.dm.service.ChatService;
import cloneproject.Instagram.global.dto.StatusResponse;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static cloneproject.Instagram.global.result.ResultCode.*;

import java.util.List;

@Api(tags = "채팅 API")
@Validated
@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@ApiOperation(value = "채팅방 생성")
	@ApiResponses({
		@ApiResponse(code = 200, message = "C001 - 채팅방 생성에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@PostMapping("/chat/rooms")
	public ResponseEntity<ResultResponse> createChatRoom(
		@RequestParam List<@NotEmpty @Size(max = 12) String> usernames) {
		final ChatRoomCreateResponse response = chatService.createRoom(usernames);

		return ResponseEntity.ok(ResultResponse.of(CREATE_CHAT_ROOM_SUCCESS, response));
	}

	@ApiOperation(value = "채팅방 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "C002 - 채팅방 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "C001 - 존재하지 않는 채팅방입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "roomId", value = "채팅방 PK", example = "1", required = true)
	@DeleteMapping("/chat/rooms/{roomId}")
	public ResponseEntity<ResultResponse> inquireChatRoom(@PathVariable Long roomId) {
		final ChatRoomInquireResponse response = chatService.inquireRoom(roomId);

		return ResponseEntity.ok(ResultResponse.of(INQUIRE_CHAT_ROOM_SUCCESS, response));
	}

	@ApiOperation(value = "채팅방 나가기")
	@ApiResponses({
		@ApiResponse(code = 200, message = "C003 - 채팅방 나가기에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "C001 - 존재하지 않는 채팅방입니다.\n"
			+ "C002 - 해당 채팅방에 참여하지 않은 회원입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "roomId", value = "채팅방 PK", example = "1", required = true)
	@DeleteMapping("/chat/rooms/hide")
	public ResponseEntity<ResultResponse> hideChatRoom(@RequestParam Long roomId) {
		final JoinRoomDeleteResponse response = chatService.deleteJoinRoom(roomId);

		return ResponseEntity.ok(ResultResponse.of(DELETE_JOIN_ROOM_SUCCESS, response));
	}

	@ApiOperation(value = "채팅방 목록 페이징 조회", notes = "페이지당 10개씩 조회할 수 있습니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "C004 - 채팅방 목록 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true)
	@GetMapping("/chat/rooms")
	public ResponseEntity<ResultResponse> getJoinRooms(@RequestParam Integer page) {
		final Page<JoinRoomDto> response = chatService.getJoinRooms(page);

		return ResponseEntity.ok(ResultResponse.of(GET_JOIN_ROOMS_SUCCESS, response));
	}

	@ApiOperation(value = "채팅방 메시지 목록 페이징 조회", notes = "페이지당 10개씩 조회할 수 있습니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "C005 - 채팅 메시지 목록 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roomId", value = "채팅방 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true)
	})
	@GetMapping("/chat/rooms/{roomId}/messages")
	public ResponseEntity<ResultResponse> getChatMessages(@PathVariable Long roomId, @RequestParam Integer page) {
		final Page<MessageDto> response = chatService.getChatMessages(roomId, page);

		return ResponseEntity.ok(ResultResponse.of(GET_CHAT_MESSAGES_SUCCESS, response));
	}

	@MessageMapping("/messages/like")
	public void likeMessage(@Valid @RequestBody MessageSimpleRequest request) {
		chatService.likeMessage(request.getMessageId(), request.getMemberId());
	}

	@MessageMapping("/messages/unlike")
	public void unlikeMessage(@Valid @RequestBody MessageSimpleRequest request) {
		chatService.unlikeMessage(request.getMessageId(), request.getMemberId());
	}

	@MessageMapping("/messages/delete")
	public void deleteMessage(@Valid @RequestBody MessageSimpleRequest request) {
		chatService.deleteMessage(request.getMessageId(), request.getMemberId());
	}

	@ApiOperation(value = "이미지 전송")
	@ApiResponses({
		@ApiResponse(code = 200, message = "C005 - 채팅 메시지 목록 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "C001 - 존재하지 않는 채팅방입니다.\n"
			+ "C002 - 해당 채팅방에 참여하지 않은 회원입니다.\n"
			+ "G007 - 지원하지 않는 이미지 타입입니다.\n"
			+ "G008 - 변환할 수 없는 파일입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roomId", value = "채팅방 PK", required = true, example = "1"),
		@ApiImplicitParam(name = "image", value = "이미지", required = true)
	})
	@PostMapping(value = "/messages/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResultResponse> sendImageMessage(@RequestParam Long roomId,
		@RequestParam MultipartFile image) {
		final StatusResponse response = chatService.sendImage(roomId, image);

		return ResponseEntity.ok(ResultResponse.of(SEND_IMAGE_SUCCESS, response));
	}

	@MessageMapping("/messages")
	public void sendTextMessage(@Valid @RequestBody MessageRequest request) {
		chatService.sendMessage(request);
	}

	@MessageMapping("/messages/indicate")
	public void indicate(@Valid @RequestBody IndicateRequest request) {
		chatService.indicate(request);
	}

	@MessageMapping("/messages/signal")
	public void signal(@Valid @RequestBody SignalRequest request) {
		chatService.signal(request);
	}

}
