package cloneproject.Instagram.entity.highlight;

import cloneproject.Instagram.vo.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "highlight_stories")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HighlightStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlight_story_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highlight_id")
    private Highlight highlight;

    @Lob
    @Column(name = "highlight_story_content")
    private String storyContent;

    @Column(name = "highlight_story_content_y")
    private Long storyContentY;

    @Column(name = "highlight_story_content_x")
    private Long storyContentX;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "imageUrl", column = @Column(name = "highlight_story_image_url")),
            @AttributeOverride(name = "imageType", column = @Column(name = "highlight_story_image_type")),
            @AttributeOverride(name = "imageName", column = @Column(name = "highlight_story_image_name")),
            @AttributeOverride(name = "imageUUID", column = @Column(name = "highlight_story_image_uuid"))
    })
    private Image image;

    @Column(name = "highlight_representation_flag")
    private boolean representationFlag;

    @Builder
    public HighlightStory(Highlight highlight, String storyContent, Long storyContentY, Long storyContentX, Image image, boolean representationFlag) {
        this.highlight = highlight;
        this.storyContent = storyContent;
        this.storyContentY = storyContentY;
        this.storyContentX = storyContentX;
        this.image = image;
        this.representationFlag = representationFlag;
    }
}
