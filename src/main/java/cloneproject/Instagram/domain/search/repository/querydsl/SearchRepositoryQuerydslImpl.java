package cloneproject.Instagram.domain.search.repository.querydsl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.domain.follow.dto.FollowDTO;
import cloneproject.Instagram.domain.search.dto.SearchDTO;
import cloneproject.Instagram.domain.search.dto.SearchHashtagDTO;
import cloneproject.Instagram.domain.search.dto.SearchMemberDTO;
import cloneproject.Instagram.domain.search.entity.Search;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.domain.follow.dto.QFollowDTO;
import cloneproject.Instagram.domain.search.dto.QSearchHashtagDTO;
import cloneproject.Instagram.domain.search.dto.QSearchMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cloneproject.Instagram.domain.member.entity.QMember.member;
import static cloneproject.Instagram.domain.hashtag.entity.QHashtag.hashtag;
import static cloneproject.Instagram.domain.search.entity.QSearch.search;
import static cloneproject.Instagram.domain.search.entity.QSearchMember.searchMember;
import static cloneproject.Instagram.domain.search.entity.QSearchHashtag.searchHashtag;
import static cloneproject.Instagram.domain.follow.entity.QFollow.follow;

@Slf4j
@RequiredArgsConstructor
public class SearchRepositoryQuerydslImpl implements SearchRepositoryQuerydsl{
    
    private final JPAQueryFactory queryFactory;
    private final MemberStoryRedisRepository memberStoryRedisRepository;

    @Override
    public List<SearchDTO> searchByText(Long loginedId, String text){

        String keyword = text + "%";

        List<Search> searches = queryFactory
            .select(search)
            .from(search)
            .where(search.id.in(
                JPAExpressions
                    .select(searchMember.id)
                    .from(searchMember)
                    .innerJoin(searchMember.member, member)
                    .where(searchMember.member.username.like(keyword)
                        .or(searchMember.member.name.like(keyword)))
            ).or(search.id.in(
                JPAExpressions
                    .select(searchHashtag.id)
                    .from(searchHashtag)
                    .innerJoin(searchHashtag.hashtag, hashtag)
                    .where(searchHashtag.hashtag.name.like(keyword))
            )))
            .orderBy(search.count.desc())
            .limit(50)
            .distinct()
            .fetch();

        final List<Long> searchIds = searches.stream()
                .map(Search::getId)
                .collect(Collectors.toList());

        Map<Long, SearchMemberDTO> memberMap = queryFactory
            .from(searchMember)
            .innerJoin(searchMember.member, member)
            .where(searchMember.id.in(searchIds))
            .transform(GroupBy.groupBy(searchMember.id).as(new QSearchMemberDTO(
                searchMember._super.dtype,
                searchMember._super.count,
                searchMember.member,
                JPAExpressions
                    .selectFrom(follow)
                    .where(follow.member.id.eq(loginedId).and(follow.followMember.id.eq(searchMember.member.id)))
                    .exists(),
                JPAExpressions
                    .selectFrom(follow)
                    .where(follow.member.id.eq(searchMember.member.id).and(follow.followMember.id.eq(loginedId)))
                    .exists()
                )
            )
        );

        // follow 주입
        final List<String> resultUsernames = memberMap.values().stream().map(s -> s.getMemberDTO().getUsername())
            .collect(Collectors.toList());

        // TODO 우선 이전의 search에 있던 코드 그대로 넣었는데 개선 필요해보임 !
        memberMap.forEach((id, member) -> {
            member.getMemberDTO().setHasStory(memberStoryRedisRepository.findById(id).isPresent());
        });

        final List<FollowDTO> follows = queryFactory
                .select(new QFollowDTO(
                        follow.member.username,
                        follow.followMember.username
                ))
                .from(follow)
                .where(follow.followMember.username.in(resultUsernames)
                        .and(follow.member.id.in(
                                JPAExpressions
                                        .select(follow.followMember.id)
                                        .from(follow)
                                        .where(follow.member.id.eq(loginedId))
                        )))
                .fetch();

        final Map<String, List<FollowDTO>> followsMap = follows.stream()
                .collect(Collectors.groupingBy(FollowDTO::getFollowMemberUsername));
        memberMap.forEach((id, member) -> member.setFollowingMemberFollow(followsMap.get(member.getMemberDTO().getUsername())));

        // 해시태그
        Map<Long, SearchHashtagDTO> hashtagMap = queryFactory
            .from(searchHashtag)
            .innerJoin(searchHashtag.hashtag, hashtag)
            .where(searchHashtag.id.in(searchIds))
            .transform(GroupBy.groupBy(searchHashtag.id).as(new QSearchHashtagDTO(
                searchHashtag.dtype,
                searchHashtag.count,
                searchHashtag.hashtag
            ))
        );

        final List<String> resultHashtagNames = hashtagMap.values().stream().map(h -> h.getName())
            .collect(Collectors.toList());

        boolean matchingMemberExists = resultUsernames.contains(text);
        boolean matchingHashtagExists = resultHashtagNames.contains(text);

        final List<SearchDTO> searchDTOs = searches.stream()
                .map(search -> {
                    switch (search.getDtype()) {
                        case "MEMBER":
                            return memberMap.get(search.getId());
                        case "HASHTAG":
                            return hashtagMap.get(search.getId());
                        default:
                            return null;
                    }
                })
                .collect(Collectors.toList());
        
        if(!matchingHashtagExists){
            SearchHashtagDTO searchHashtagDTO = queryFactory
                .select(new QSearchHashtagDTO(
                    searchHashtag.dtype,
                    searchHashtag.count,
                    searchHashtag.hashtag
                ))
                .from(searchHashtag)
                .innerJoin(searchHashtag.hashtag, hashtag)
                .where(searchHashtag.hashtag.name.eq(text))
                .fetchFirst();
            if(searchHashtagDTO != null) {
                searchDTOs.add(0, searchHashtagDTO);
                searchDTOs.remove(searchDTOs.size()-1);
            }
        }
        if(!matchingMemberExists){
            SearchMemberDTO searchMemberDTO = queryFactory
                .select(new QSearchMemberDTO(
                    searchMember._super.dtype,
                    searchMember._super.count,
                    searchMember.member,
                    JPAExpressions
                        .selectFrom(follow)
                        .where(follow.member.id.eq(loginedId).and(follow.followMember.id.eq(searchMember.member.id)))
                        .exists(),
                    JPAExpressions
                        .selectFrom(follow)
                        .where(follow.member.id.eq(searchMember.member.id).and(follow.followMember.id.eq(loginedId)))
                        .exists()
                ))
                .from(searchMember)
                .innerJoin(searchMember.member, member)
                .where(searchMember.member.username.eq(text))
                .fetchFirst();
            if(searchMemberDTO != null) {
                searchDTOs.add(0, searchMemberDTO);
                searchDTOs.remove(searchDTOs.size()-1);
            }
        }

        return searchDTOs;
    }


    @Override
    public List<SearchDTO> searchByTextOnlyHashtag(Long loginedId, String text){
        String keyword = text + "%";

        List<Search> searches = queryFactory
            .select(search)
            .from(search)
            .where(search.id.in(
                JPAExpressions
                    .select(searchHashtag.id)
                    .from(searchHashtag)
                    .innerJoin(searchHashtag.hashtag, hashtag)
                    .where(searchHashtag.hashtag.name.like(keyword))
            ))
            .orderBy(search.count.desc())
            .limit(50)
            .distinct()
            .fetch();

        final List<Long> searchIds = searches.stream()
                .map(Search::getId)
                .collect(Collectors.toList());

        Map<Long, SearchHashtagDTO> hashtagMap = queryFactory
            .from(searchHashtag)
            .innerJoin(searchHashtag.hashtag, hashtag)
            .where(searchHashtag.id.in(searchIds))
            .transform(GroupBy.groupBy(searchHashtag.id).as(new QSearchHashtagDTO(
                searchHashtag.dtype,
                searchHashtag.count,
                searchHashtag.hashtag
            ))
        );

        boolean matchingHashtagExists = queryFactory
            .from(searchHashtag)
            .innerJoin(searchHashtag.hashtag, hashtag).fetchJoin()
            .where(searchHashtag.id.in(searchIds).and(searchHashtag.hashtag.name.eq(text)))
            .fetchFirst() != null;

        final List<SearchDTO> searchDTOs = searches.stream()
                .map(search -> {
                            return hashtagMap.get(search.getId());
                    
                })
                .collect(Collectors.toList());
        
        if(!matchingHashtagExists){
            SearchHashtagDTO searchHashtagDTO = queryFactory
                .select(new QSearchHashtagDTO(
                    searchHashtag.dtype,
                    searchHashtag.count,
                    searchHashtag.hashtag
                ))
                .from(searchHashtag)
                .innerJoin(searchHashtag.hashtag, hashtag)
                .where(searchHashtag.hashtag.name.eq(text))
                .fetchFirst();
            if(searchHashtagDTO != null) {
                searchDTOs.add(0, searchHashtagDTO);
                searchDTOs.remove(searchDTOs.size()-1);
            }
        }

        return searchDTOs;
    }
}
