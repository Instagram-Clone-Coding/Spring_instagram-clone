package cloneproject.Instagram.domain.hashtag.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.feed.entity.Post;

@Getter
@Entity
@Table(name = "hashtag_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashtagPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hashtag_post_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hashtag_id")
	private Hashtag hashtag;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	public HashtagPost(Hashtag hashtag, Post post) {
		this.hashtag = hashtag;
		this.post = post;
	}

}
