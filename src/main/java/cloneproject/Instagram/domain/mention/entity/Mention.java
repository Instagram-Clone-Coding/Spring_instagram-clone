package cloneproject.Instagram.domain.mention.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@Entity
@Table(name = "mentions")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mention {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mention_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "mention_type")
	private MentionType type;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "agent_id")
	private Member agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_id")
	private Member target;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;

	@CreatedDate
	@Column(name = "mention_create_date")
	private LocalDateTime createdDate;

	@Builder
	public Mention(MentionType type, Member agent, Member target, Post post, Comment comment) {
		this.type = type;
		this.agent = agent;
		this.target = target;
		this.post = post;
		this.comment = comment;
	}

}
