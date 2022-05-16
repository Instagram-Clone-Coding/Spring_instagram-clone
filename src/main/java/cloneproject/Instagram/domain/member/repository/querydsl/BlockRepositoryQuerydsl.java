package cloneproject.Instagram.domain.member.repository.querydsl;

public interface BlockRepositoryQuerydsl {

    boolean isBlockingOrIsBlocked(Long loginUserId, Long targetMemberId);

}
