package cloneproject.Instagram.domain.dm.service;

import cloneproject.Instagram.domain.dm.dto.ChatRoomCreateResponse;
import cloneproject.Instagram.domain.dm.dto.ChatRoomInquireResponse;
import cloneproject.Instagram.domain.dm.dto.IndicateDto;
import cloneproject.Instagram.domain.dm.dto.IndicateRequest;
import cloneproject.Instagram.domain.dm.dto.JoinRoomDto;
import cloneproject.Instagram.domain.dm.dto.JoinRoomDeleteResponse;
import cloneproject.Instagram.domain.dm.dto.MemberSimpleInfo;
import cloneproject.Instagram.domain.dm.dto.MessageAction;
import cloneproject.Instagram.domain.dm.dto.MessageDto;
import cloneproject.Instagram.domain.dm.dto.MessageRequest;
import cloneproject.Instagram.domain.dm.dto.MessageResponse;
import cloneproject.Instagram.domain.dm.dto.MessageSeenDto;
import cloneproject.Instagram.domain.dm.dto.MessageSimpleDto;
import cloneproject.Instagram.domain.dm.entity.JoinRoom;
import cloneproject.Instagram.domain.dm.entity.Message;
import cloneproject.Instagram.domain.dm.entity.MessageImage;
import cloneproject.Instagram.domain.dm.entity.MessageLike;
import cloneproject.Instagram.domain.dm.entity.MessagePost;
import cloneproject.Instagram.domain.dm.entity.MessageStory;
import cloneproject.Instagram.domain.dm.entity.MessageText;
import cloneproject.Instagram.domain.dm.entity.Room;
import cloneproject.Instagram.domain.dm.entity.RoomMember;
import cloneproject.Instagram.domain.dm.entity.RoomUnreadMember;
import cloneproject.Instagram.domain.dm.exception.ChatRoomNotFoundException;
import cloneproject.Instagram.domain.dm.exception.JoinRoomNotFoundException;
import cloneproject.Instagram.domain.dm.exception.MessageLikeAlreadyExistException;
import cloneproject.Instagram.domain.dm.exception.MessageLikeNotFoundException;
import cloneproject.Instagram.domain.dm.exception.MessageNotFoundException;
import cloneproject.Instagram.domain.dm.repository.JoinRoomRepository;
import cloneproject.Instagram.domain.dm.repository.MessageImageRepository;
import cloneproject.Instagram.domain.dm.repository.MessageLikeRepository;
import cloneproject.Instagram.domain.dm.repository.MessagePostRepository;
import cloneproject.Instagram.domain.dm.repository.MessageRepository;
import cloneproject.Instagram.domain.dm.repository.MessageStoryRepository;
import cloneproject.Instagram.domain.dm.repository.MessageTextRepository;
import cloneproject.Instagram.domain.dm.repository.RoomMemberRepository;
import cloneproject.Instagram.domain.dm.repository.RoomRepository;
import cloneproject.Instagram.domain.dm.repository.RoomUnreadMemberRepository;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.dto.StatusResponse;
import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.ErrorResponse;
import cloneproject.Instagram.global.error.exception.InvalidInputException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.infra.aws.S3Uploader;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

	private final RoomRepository roomRepository;
	private final RoomMemberRepository roomMemberRepository;
	private final RoomUnreadMemberRepository roomUnreadMemberRepository;
	private final MessageRepository messageRepository;
	private final MessageLikeRepository messageLikeRepository;
	private final MessageImageRepository messageImageRepository;
	private final MemberRepository memberRepository;
	private final JoinRoomRepository joinRoomRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final MessageTextRepository messageTextRepository;
	private final MessageStoryRepository messageStoryRepository;
	private final MessagePostRepository messagePostRepository;
	private final S3Uploader uploader;
	private final AuthUtil authUtil;

	@Transactional
	public ChatRoomCreateResponse createRoom(List<String> usernames) {
		final Member inviter = authUtil.getLoginMember();
		usernames.add(inviter.getUsername());
		final List<Member> members = memberRepository.findAllByUsernameIn(usernames);

		final Room room;
		final boolean status;
		final Optional<Room> roomOptional = getRoomByMembers(members);
		if (roomOptional.isEmpty()) {
			status = true;
			room = roomRepository.save(new Room(inviter));
			roomMemberRepository.saveAllBatch(room, members);
		} else {
			status = false;
			room = roomOptional.get();
		}

		final List<MemberSimpleInfo> memberSimpleInfos = members.stream()
			.map(MemberSimpleInfo::new)
			.collect(Collectors.toList());

		return new ChatRoomCreateResponse(status, room.getId(), new MemberSimpleInfo(inviter), memberSimpleInfos);
	}

	private Optional<Room> getRoomByMembers(List<Member> members) {
		final Map<Long, List<RoomMember>> roomMembersMap = roomMemberRepository.findAllByMemberIn(members)
			.stream()
			.collect(Collectors.groupingBy(r -> r.getRoom().getId()));

		final List<Long> roomIds = new ArrayList<>();
		roomMembersMap.forEach((rid, rms) -> {
			if (rms.size() == members.size()) {
				roomIds.add(rid);
			}
		});
		final Map<Long, List<RoomMember>> roomMemberMapGroupByRoomId = roomMemberRepository.findAllByRoomIdIn(roomIds)
			.stream()
			.collect(Collectors.groupingBy(r -> r.getRoom().getId()));

		for (final Long roomId : roomMemberMapGroupByRoomId.keySet()) {
			if (roomMemberMapGroupByRoomId.get(roomId).size() == members.size()) {
				return Optional.of(roomMemberMapGroupByRoomId.get(roomId).get(0).getRoom());
			}
		}

		return Optional.empty();
	}

	@Transactional
	public ChatRoomInquireResponse inquireRoom(Long roomId) {
		final LocalDateTime now = LocalDateTime.now();
		final Member loginMember = authUtil.getLoginMember();
		final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
		final Map<Long, List<RoomUnreadMember>> roomUnreadMemberMap = roomUnreadMemberRepository.findAllByRoom(room)
			.stream()
			.collect(Collectors.groupingBy(r -> r.getMember().getId()));

		long unseenCount = 0;
		for (final Long id : roomUnreadMemberMap.keySet()) {
			if (!roomUnreadMemberMap.get(id).isEmpty()) {
				unseenCount++;
			}
		}

		if (roomUnreadMemberRepository.findAllByRoomAndMember(room, loginMember).isEmpty()) {
			return new ChatRoomInquireResponse(false, unseenCount);
		}

		final List<JoinRoom> joinRooms = joinRoomRepository.findAllWithMemberByRoomId(roomId);
		final MessageSeenDto messageSeenDTO = new MessageSeenDto(room.getId(), loginMember.getId(), now);
		final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_SEEN, messageSeenDTO);
		joinRooms.forEach(joinRoom -> {
			if (!joinRoom.getMember().getId().equals(loginMember.getId())) {
				messagingTemplate.convertAndSend("/sub/" + joinRoom.getMember().getUsername(), response);
			}
		});

		final List<RoomUnreadMember> roomUnreadMembers = roomUnreadMemberRepository.findAllByRoomAndMember(room,
			loginMember);
		roomUnreadMemberRepository.deleteAllInBatch(roomUnreadMembers);

		return new ChatRoomInquireResponse(true, unseenCount - 1);
	}

	@Transactional
	public JoinRoomDeleteResponse deleteJoinRoom(Long roomId) {
		final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
		final Member loginMember = authUtil.getLoginMember();

		if (joinRoomRepository.findByMemberAndRoom(loginMember, room).isEmpty()) {
			throw new JoinRoomNotFoundException();
		}
		joinRoomRepository.deleteByMemberAndRoom(loginMember, room);

		return new JoinRoomDeleteResponse(true);
	}

	public Page<JoinRoomDto> getJoinRooms(int page) {
		final Member loginMember = authUtil.getLoginMember();
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, 10, Sort.by(DESC, "id"));
		final Page<JoinRoomDto> joinRoomDtoPage = joinRoomRepository.findJoinRoomDtoPageByMemberId(loginMember.getId(),
			pageable);

		final List<JoinRoomDto> joinRoomDtos = joinRoomDtoPage.getContent();
		final List<Long> roomIds = joinRoomDtos.stream()
			.map(JoinRoomDto::getRoomId)
			.collect(Collectors.toList());
		final List<Long> messageIds = joinRoomRepository.findAllWithMessageByMemberIdAndRoomIdIn(loginMember.getId(),
				roomIds).stream()
			.map(JoinRoom::getMessage)
			.map(Message::getId)
			.collect(Collectors.toList());

		setMemberAndLastMessageToJoinRoomDto(joinRoomDtos, roomIds, messageIds);

		return joinRoomDtoPage;
	}

	private void setMemberAndLastMessageToJoinRoomDto(List<JoinRoomDto> joinRoomDtos, List<Long> roomIds,
		List<Long> messageIds) {
		final Map<Long, MessagePost> messagePostMap = messagePostRepository.findAllWithPostByIdIn(messageIds)
			.stream()
			.collect(Collectors.toMap(mp -> mp.getRoom().getId(), mp -> mp));
		final Map<Long, MessageImage> messageImageMap = messageImageRepository.findAllByIdIn(messageIds)
			.stream()
			.collect(Collectors.toMap(mi -> mi.getRoom().getId(), mi -> mi));
		final Map<Long, MessageText> messageTextMap = messageTextRepository.findAllByIdIn(messageIds)
			.stream()
			.collect(Collectors.toMap(mt -> mt.getRoom().getId(), mt -> mt));

		final Map<Long, List<RoomMember>> roomMembersMap = roomMemberRepository.findAllWithMemberByRoomIdIn(roomIds)
			.stream()
			.collect(Collectors.groupingBy(rm -> rm.getRoom().getId()));
		joinRoomDtos.forEach(joinRoomDto -> {
			joinRoomDto.setMembers(
				roomMembersMap.get(joinRoomDto.getRoomId()).stream()
					.map(r -> new MemberSimpleInfo(r.getMember()))
					.collect(Collectors.toList())
			);

			if (messagePostMap.containsKey(joinRoomDto.getRoomId())) {
				joinRoomDto.setLastMessage(new MessageDto(messagePostMap.get(joinRoomDto.getRoomId())));
			} else if (messageImageMap.containsKey(joinRoomDto.getRoomId())) {
				joinRoomDto.setLastMessage(new MessageDto(messageImageMap.get(joinRoomDto.getRoomId())));
			} else if (messageTextMap.containsKey(joinRoomDto.getRoomId())) {
				joinRoomDto.setLastMessage(new MessageDto(messageTextMap.get(joinRoomDto.getRoomId())));
			}
		});
	}

	@Transactional
	public void sendMessage(MessageRequest request) {
		final Member sender = memberRepository.findById(request.getSenderId())
			.orElseThrow(MemberDoesNotExistException::new);
		final Room room = roomRepository.findById(request.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);
		final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(room.getId());
		if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(sender.getId())))
			throw new JoinRoomNotFoundException();

		final MessageText message = messageTextRepository.save(new MessageText(request.getContent(), sender, room));
		message.setDtype();
		updateRoom(request.getSenderId(), room, roomMembers, message);

		final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDto(message));
		roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
	}

	public void indicate(IndicateRequest request) {
		final Room room = roomRepository.findById(request.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);
		final List<JoinRoom> joinRooms = joinRoomRepository.findAllWithMemberByRoomId(room.getId());
		final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_ACK,
			new IndicateDto(room.getId(), request.getSenderId(), 12000L));

		joinRooms.forEach(j -> {
			if (!j.getMember().getId().equals(request.getSenderId())) {
				messagingTemplate.convertAndSend("/sub/" + j.getMember().getUsername(), response);
			}
		});
	}

	public Page<MessageDto> getChatMessages(Long roomId, Integer page) {
		final Long memberId = authUtil.getLoginMemberId();
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, 10);

		final JoinRoom joinRoom = joinRoomRepository.findWithRoomAndMemberByMemberIdAndRoomId(memberId, roomId)
			.orElseThrow(JoinRoomNotFoundException::new);
		final Page<Message> messagePage = messageRepository.findAllByJoinRoom(joinRoom, pageable);

		final List<Message> messages = messagePage.getContent();
		final List<Long> messageIds = messages.stream()
			.map(Message::getId)
			.collect(Collectors.toList());

		final List<MessageDto> messageDtos = convertToDto(messages, messageIds);
		setMessageLikesToMessageDto(messageIds, messageDtos);

		return new PageImpl<>(messageDtos, pageable, messagePage.getTotalElements());
	}

	private void setMessageLikesToMessageDto(List<Long> messageIds, List<MessageDto> messageDtos) {
		final Map<Long, List<MessageLike>> messageLikesMap = messageLikeRepository.findAllWithMemberByMessageIdIn(
				messageIds)
			.stream()
			.collect(Collectors.groupingBy(ml -> ml.getMessage().getId()));
		messageDtos.forEach(m -> {
			if (messageLikesMap.containsKey(m.getMessageId())) {
				final List<MemberDto> likeMembers = messageLikesMap.get(m.getMessageId()).stream()
					.map(ml -> new MemberDto(ml.getMember()))
					.collect(Collectors.toList());
				m.setLikeMembers(likeMembers);
			}
		});
	}

	private List<MessageDto> convertToDto(List<Message> messages, List<Long> messageIds) {
		final Map<Long, MessageStory> messageStoryMap = messageStoryRepository.findAllWithStoryByIdIn(messageIds)
			.stream()
			.collect(Collectors.toMap(Message::getId, ms -> ms));
		final Map<Long, MessagePost> messagePostMap = messagePostRepository.findAllWithPostByIdIn(messageIds)
			.stream()
			.collect(Collectors.toMap(Message::getId, mp -> mp));
		final Map<Long, MessageImage> messageImageMap = messageImageRepository.findAllByIdIn(messageIds)
			.stream()
			.collect(Collectors.toMap(Message::getId, mi -> mi));
		final Map<Long, MessageText> messageTextMap = messageTextRepository.findAllByIdIn(messageIds)
			.stream()
			.collect(Collectors.toMap(Message::getId, mt -> mt));

		return messages.stream()
			.map(m -> {
				switch (m.getDtype()) {
					case "POST":
						return new MessageDto(messagePostMap.get(m.getId()));
					case "STORY":
						return new MessageDto(messageStoryMap.get(m.getId()));
					case "IMAGE":
						return new MessageDto(messageImageMap.get(m.getId()));
					case "TEXT":
						return new MessageDto(messageTextMap.get(m.getId()));
				}
				return null;
			})
			.collect(Collectors.toList());
	}

	@Transactional
	public StatusResponse sendImage(Long roomId, MultipartFile multipartFile) {
		if (multipartFile.isEmpty()) {
			final List<ErrorResponse.FieldError> errors = new ArrayList<>();
			errors.add(new ErrorResponse.FieldError("image", "null", ErrorCode.MESSAGE_IMAGE_INVALID.getMessage()));
			throw new InvalidInputException(errors);
		}

		final Member loginMember = authUtil.getLoginMember();
		final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
		final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(room.getId());
		if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(loginMember.getId()))) {
			throw new JoinRoomNotFoundException();
		}

		final Image image = uploader.uploadImage(multipartFile, "chat");
		final MessageImage message = messageImageRepository.save(new MessageImage(image, loginMember, room));
		message.setDtype();
		updateRoom(loginMember.getId(), room, roomMembers, message);

		final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDto(message));
		roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));

		return new StatusResponse(true);
	}

	private void updateRoom(Long senderId, Room room, List<RoomMember> roomMembers, Message message) {
		final List<Member> members = roomMembers.stream()
			.map(RoomMember::getMember)
			.collect(Collectors.toList());
		final Map<Long, JoinRoom> joinRoomMap = joinRoomRepository.findByRoomAndMemberIn(room, members).stream()
			.collect(Collectors.toMap(j -> j.getMember().getId(), j -> j));

		final List<JoinRoom> newJoinRooms = new ArrayList<>();
		final List<JoinRoom> updateJoinRooms = new ArrayList<>();
		final List<RoomUnreadMember> newRoomUnreadMembers = new ArrayList<>();

		for (final RoomMember roomMember : roomMembers) {
			final Member member = roomMember.getMember();
			if (!member.getId().equals(senderId)) {
				newRoomUnreadMembers.add(new RoomUnreadMember(room, message, member));
			}
			if (joinRoomMap.containsKey(member.getId())) {
				updateJoinRooms.add(joinRoomMap.get(member.getId()));
			} else {
				newJoinRooms.add(new JoinRoom(room, member, message));
			}
		}

		roomUnreadMemberRepository.saveAllBatch(newRoomUnreadMembers, message);
		joinRoomRepository.saveAllBatch(newJoinRooms, message);
		joinRoomRepository.updateAllBatch(updateJoinRooms, message);
	}

	@Transactional
	public void deleteMessage(Long messageId, Long memberId) {
		final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
		final Message message = messageRepository.findWithRoomById(messageId)
			.orElseThrow(MessageNotFoundException::new);

		if (!message.getMember().getId().equals(member.getId())) {
			final List<ErrorResponse.FieldError> errors = new ArrayList<>();
			errors.add(new ErrorResponse.FieldError("memberId", memberId.toString(),
				ErrorCode.MESSAGE_SENDER_MISMATCH.getMessage()));
			throw new InvalidInputException(errors);
		}

		final List<RoomUnreadMember> roomUnreadMembers = roomUnreadMemberRepository.findAllByMessage(message);
		roomUnreadMemberRepository.deleteAllInBatch(roomUnreadMembers);

		final Room room = message.getRoom();
		final List<JoinRoom> joinRooms = joinRoomRepository.findAllWithMessageByRoomId(room.getId());
		joinRooms.forEach(joinRoom -> {
			final LocalDateTime createdDateOfMessageToDelete = message.getCreatedDate();
			final LocalDateTime createdDateOfJoinRoom = joinRoom.getCreatedDate();

			if (!createdDateOfMessageToDelete.isBefore(createdDateOfJoinRoom)) {
				if (message.equals(joinRoom.getMessage())) {
					final LocalDateTime start = createdDateOfJoinRoom.minusSeconds(1L);
					final LocalDateTime end = createdDateOfMessageToDelete.plusSeconds(1L);
					final Long total = messageRepository.countByCreatedDateBetweenAndRoom(start, end, room);

					if (total == 1) {
						joinRoomRepository.delete(joinRoom);
					} else {
						final List<Message> messages = messageRepository.findTop2ByCreatedDateBetweenAndRoomOrderByIdDesc(
							start, end, room);
						joinRoom.updateMessage(messages.get(1));
					}
				}
			}
		});

		final MessageSimpleDto messageSimpleDto = new MessageSimpleDto(message, member);
		final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_DELETE, messageSimpleDto);
		messageRepository.delete(message);

		final List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(room);
		roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
	}

	@Transactional
	public void likeMessage(Long messageId, Long memberId) {
		final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
		final Message message = messageRepository.findWithRoomById(messageId)
			.orElseThrow(MessageNotFoundException::new);

		final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(message.getRoom().getId());
		if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(member.getId()))) {
			throw new JoinRoomNotFoundException();
		}

		if (messageLikeRepository.findByMemberAndMessage(member, message).isPresent()) {
			throw new MessageLikeAlreadyExistException();
		}
		messageLikeRepository.save(new MessageLike(member, message));

		final MessageSimpleDto messageSimpleDto = new MessageSimpleDto(message, member);
		final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_LIKE, messageSimpleDto);
		roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
	}

	@Transactional
	public void unlikeMessage(Long messageId, Long memberId) {
		final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
		final Message message = messageRepository.findWithRoomById(messageId)
			.orElseThrow(MessageNotFoundException::new);

		final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(message.getRoom().getId());
		if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(member.getId()))) {
			throw new JoinRoomNotFoundException();
		}

		final Optional<MessageLike> findMessageLike = messageLikeRepository.findByMemberAndMessage(member, message);
		if (findMessageLike.isEmpty()) {
			throw new MessageLikeNotFoundException();
		}
		messageLikeRepository.delete(findMessageLike.get());

		final MessageSimpleDto messageSimpleDto = new MessageSimpleDto(message, member);
		final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_UNLIKE, messageSimpleDto);
		roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
	}

}
