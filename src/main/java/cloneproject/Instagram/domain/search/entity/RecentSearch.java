package cloneproject.Instagram.domain.search.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "recent_searches")
@IdClass(RecentSearch.RecentSearchId.class)
public class RecentSearch {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "search_id")
	private Search search;
	@Column(name = "recent_search_last_searched_date")
	private LocalDateTime lastSearchedDate;

	@Builder
	public RecentSearch(Member member, Search search) {
		this.member = member;
		this.search = search;
	}

	public void updateLastSearchedDate() {
		this.lastSearchedDate = LocalDateTime.now();
	}

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	static class RecentSearchId implements Serializable {

		private Long member;
		private Long search;

	}

}
