package cloneproject.Instagram.domain.story.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberStoryService {

	private final MemberStoryRedisRepository memberStoryRedisRepository;

	public void setHasStoryInMemberDtos(List<MemberDto> memberDtos) {
		memberDtos.forEach(memberDto -> memberDto.setHasStory(existsMemberStory(memberDto.getId())));
	}

	private boolean existsMemberStory(Long memberId) {
		return !memberStoryRedisRepository.findAllByMemberId(memberId).isEmpty();
	}

}
