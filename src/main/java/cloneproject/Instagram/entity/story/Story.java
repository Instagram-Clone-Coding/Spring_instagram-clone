package cloneproject.Instagram.entity.story;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.vo.Image;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Lob
    @Column(name = "story_content")
    private String content;

    @Column(name = "story_content_y")
    private Long contentY;

    @Column(name = "sotry_content_x")
    private Long contentX;

    @Column(name = "story_upload_date")
    @CreatedDate
    private LocalDateTime uploadDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "imageUrl", column = @Column(name = "story_image_url")),
            @AttributeOverride(name = "imageType", column = @Column(name = "story_image_type")),
            @AttributeOverride(name = "imageName", column = @Column(name = "story_image_name")),
            @AttributeOverride(name = "imageUUID", column = @Column(name = "story_image_uuid"))
    })
    private Image image;

    @Builder
    public Story(Member member, String content, Long contentY, Long contentX, Image image) {
        this.member = member;
        this.content = content;
        this.contentY = contentY;
        this.contentX = contentX;
        this.image = image;
    }
}