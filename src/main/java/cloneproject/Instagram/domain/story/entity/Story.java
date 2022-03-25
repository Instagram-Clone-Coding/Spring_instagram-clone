package cloneproject.Instagram.domain.story.entity;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.global.vo.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    public Story(Member member, Image image) {
        this.member = member;
        this.image = image;
    }
}