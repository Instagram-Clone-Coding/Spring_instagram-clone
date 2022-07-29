package cloneproject.Instagram.domain.feed.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Lob
	@Column(name = "post_content")
	private String content;

	@CreatedDate
	@Column(name = "post_upload_date")
	private LocalDateTime uploadDate;

	@OneToMany(mappedBy = "post")
	private List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "post")
	private List<PostLike> postLikes = new ArrayList<>();

	@OneToMany(mappedBy = "post")
	private List<Bookmark> bookmarks = new ArrayList<>();

	@OneToMany(mappedBy = "post")
	private List<PostImage> postImages = new ArrayList<>();

	@Column(name = "post_comment_flag")
	private boolean commentFlag;

	@Column(name = "post_like_flag")
	private boolean likeFlag;

	@Builder
	public Post(Member member, String content, boolean commentFlag, boolean likeFlag) {
		this.member = member;
		this.content = content;
		this.commentFlag = commentFlag;
		this.likeFlag = likeFlag;
	}

}
