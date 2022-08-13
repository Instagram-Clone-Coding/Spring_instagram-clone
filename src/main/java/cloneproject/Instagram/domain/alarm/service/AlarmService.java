package cloneproject.Instagram.domain.alarm.service;

import static cloneproject.Instagram.domain.alarm.dto.AlarmType.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.alarm.dto.AlarmContentDto;
import cloneproject.Instagram.domain.alarm.dto.AlarmDto;
import cloneproject.Instagram.domain.alarm.dto.AlarmFollowDto;
import cloneproject.Instagram.domain.alarm.dto.AlarmType;
import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.alarm.exception.MismatchedAlarmTypeException;
import cloneproject.Instagram.domain.alarm.repository.AlarmRepository;
import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.util.StringExtractUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

	private final AlarmRepository alarmRepository;
	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;
	private final StringExtractUtil stringExtractUtil;
	private final MemberStoryRedisRepository memberStoryRedisRepository;
	private final AuthUtil authUtil;

	public Page<AlarmDto> getAlarms(int page, int size) {
		final Member loginMember = authUtil.getLoginMember();
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, size);

		final Page<Alarm> alarmPage = alarmRepository.findAlarmPageByMemberId(pageable, loginMember.getId());
		final List<Alarm> alarms = alarmPage.getContent();
		final List<Long> agentIds = alarms.stream()
			.filter(a -> a.getType().equals(FOLLOW))
			.map(a -> a.getAgent().getId())
			.collect(Collectors.toList());

		final List<Follow> follows = followRepository.findFollows(loginMember.getId(), agentIds);
		final Map<Long, Follow> followMap = follows.stream()
			.collect(Collectors.toMap(f -> f.getFollowMember().getId(), f -> f));

		final List<AlarmDto> content = convertToDto(alarms, followMap);
		setHasStory(content);

		return new PageImpl<>(content, pageable, alarmPage.getTotalElements());
	}

	@Transactional
	public void alert(Member target, Follow follow) {
		final Member loginMember = authUtil.getLoginMember();
		final Alarm alarm = Alarm.builder()
			.type(FOLLOW)
			.agent(loginMember)
			.target(target)
			.follow(follow)
			.build();

		alarmRepository.save(alarm);
	}

	@Transactional
	public void alert(AlarmType type, Member target, Post post) {
		if (!type.equals(LIKE_POST)) {
			throw new MismatchedAlarmTypeException();
		}

		final Member loginMember = authUtil.getLoginMember();
		final Alarm alarm = Alarm.builder()
			.type(type)
			.agent(loginMember)
			.target(target)
			.post(post)
			.build();

		alarmRepository.save(alarm);
	}

	@Transactional
	public void alertBatch(AlarmType type, List<Member> targets, Post post) {
		if (!type.equals(MENTION_POST)) {
			throw new MismatchedAlarmTypeException();
		}

		final Member loginMember = authUtil.getLoginMember();
		alarmRepository.saveMentionPostAlarms(loginMember, targets, post, LocalDateTime.now());
	}

	@Transactional
	public void alertBatch(AlarmType type, List<Member> targets, Post post, Comment comment) {
		if (!type.equals(MENTION_COMMENT)) {
			throw new MismatchedAlarmTypeException();
		}

		final Member loginMember = authUtil.getLoginMember();
		alarmRepository.saveMentionCommentAlarms(loginMember, targets, post, comment, LocalDateTime.now());
	}

	@Transactional
	public void alert(AlarmType type, Member target, Post post, Comment comment) {
		if (!type.equals(COMMENT) && !type.equals(LIKE_COMMENT) && !type.equals(MENTION_COMMENT)) {
			throw new MismatchedAlarmTypeException();
		}

		final Member loginMember = authUtil.getLoginMember();
		final Alarm alarm = Alarm.builder()
			.type(type)
			.agent(loginMember)
			.target(target)
			.post(post)
			.comment(comment)
			.build();

		alarmRepository.save(alarm);
	}

	@Transactional
	public void delete(AlarmType type, Member target, Post post) {
		final Member loginMember = authUtil.getLoginMember();
		alarmRepository.deleteByTypeAndAgentAndTargetAndPost(type, loginMember, target, post);
	}

	@Transactional
	public void delete(AlarmType type, Member target, Comment comment) {
		final Member loginMember = authUtil.getLoginMember();
		alarmRepository.deleteByTypeAndAgentAndTargetAndComment(type, loginMember, target, comment);
	}

	@Transactional
	public void delete(Member target, Follow follow) {
		final Member loginMember = authUtil.getLoginMember();
		alarmRepository.deleteByTypeAndAgentAndTargetAndFollow(FOLLOW, loginMember, target, follow);
	}

	@Transactional
	public void deleteAll(Post post) {
		final List<Alarm> alarms = alarmRepository.findAllByPost(post);
		alarmRepository.deleteAllInBatch(alarms);
	}

	@Transactional
	public void deleteAll(List<Comment> comments) {
		final List<Alarm> alarms = alarmRepository.findAllByCommentIn(comments);
		alarmRepository.deleteAllInBatch(alarms);
	}

	private void setHasStory(List<AlarmDto> content) {
		content.forEach(alarm -> {
			final MemberDto agent = alarm.getAgent();
			final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(agent.getId()).size() > 0;
			agent.setHasStory(hasStory);
		});
	}

	private List<AlarmDto> convertToDto(List<Alarm> alarms, Map<Long, Follow> followMap) {
		return alarms.stream()
			.map(alarm -> {
				final AlarmType type = alarm.getType();
				if (type.equals(FOLLOW)) {
					return new AlarmFollowDto(alarm, followMap.containsKey(alarm.getAgent().getId()));
				} else {
					final AlarmContentDto dto = new AlarmContentDto(alarm);
					if (type.equals(COMMENT) || type.equals(LIKE_COMMENT) || type.equals(MENTION_COMMENT)) {
						setMentionAndHashtagList(alarm.getComment().getContent(), dto);
					} else if (type.equals(LIKE_POST) || type.equals(MENTION_POST)) {
						setMentionAndHashtagList(alarm.getPost().getContent(), dto);
					}
					return dto;
				}
			})
			.collect(Collectors.toList());
	}

	private void setMentionAndHashtagList(String content, AlarmContentDto dto) {
		final List<String> mentionedUsernames = stringExtractUtil.extractMentions(content, List.of());
		final List<String> existentUsernames = memberRepository.findAllByUsernameIn(mentionedUsernames).stream()
			.map(Member::getUsername)
			.collect(Collectors.toList());
		dto.setExistentMentionsOfContent(existentUsernames);
		mentionedUsernames.removeAll(existentUsernames);
		dto.setNonExistentMentionsOfContent(mentionedUsernames);
		final List<String> hashtags = stringExtractUtil.extractHashtags(content);
		dto.setHashtagsOfContent(hashtags);
	}

}
