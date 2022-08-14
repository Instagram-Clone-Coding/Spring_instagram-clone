package cloneproject.Instagram.domain.story.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.global.vo.Image;

@Getter
@Entity
@Table(name = "stories")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Story {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "story_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "imageUrl", column = @Column(name = "story_image_url")),
		@AttributeOverride(name = "imageType", column = @Column(name = "story_image_type")),
		@AttributeOverride(name = "imageName", column = @Column(name = "story_image_name")),
		@AttributeOverride(name = "imageUUID", column = @Column(name = "story_image_uuid"))
	})
	private Image image;

	@Column(name = "story_upload_date")
	@CreatedDate
	private LocalDateTime uploadDate;

	@Builder
	public Story(Member member, Image image) {
		this.member = member;
		this.image = image;
	}

}
