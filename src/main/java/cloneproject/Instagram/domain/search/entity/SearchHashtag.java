package cloneproject.Instagram.domain.search.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.hashtag.entity.Hashtag;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("HASHTAG")
@Table(name = "search_hashtags")
public class SearchHashtag extends Search {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hashtag_id")
	private Hashtag hashtag;

	public SearchHashtag(Hashtag hashtag) {
		super();
		this.hashtag = hashtag;
	}

}
