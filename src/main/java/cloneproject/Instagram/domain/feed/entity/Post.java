package cloneproject.Instagram.domain.feed.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import cloneproject.Instagram.domain.member.entity.Member;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	@Builder
	public Post(Member member, String content, boolean commentFlag) {
		this.member = member;
		this.content = content;
		this.commentFlag = commentFlag;
	}

}
