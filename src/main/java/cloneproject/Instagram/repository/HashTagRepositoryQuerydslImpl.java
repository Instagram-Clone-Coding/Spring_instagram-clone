package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.hashtag.HashtagDTO;
import cloneproject.Instagram.entity.hashtag.Hashtag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static cloneproject.Instagram.entity.hashtag.QHashtag.hashtag;

@RequiredArgsConstructor
public class HashTagRepositoryQuerydslImpl implements HashTagRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HashtagDTO> findHashtagDtoPageLikeName(Pageable pageable, String name) {
        final List<Hashtag> hashtags = queryFactory
                .selectFrom(hashtag)
                .where(hashtag.name.like(name + "%"))
                .orderBy(hashtag.count.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        final long total = queryFactory
                .selectFrom(hashtag)
                .where(hashtag.name.like(name + "%"))
                .fetchCount();

        final List<HashtagDTO> hashtagDTOs = hashtags.stream()
                .map(HashtagDTO::new)
                .collect(Collectors.toList());

        return new PageImpl<>(hashtagDTOs, pageable, total);
    }
}
