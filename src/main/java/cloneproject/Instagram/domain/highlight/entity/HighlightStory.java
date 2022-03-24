package cloneproject.Instagram.domain.highlight.entity;

import cloneproject.Instagram.domain.story.entity.Story;
import cloneproject.Instagram.global.vo.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "highlight_stories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HighlightStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlight_story_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highlight_id")
    private Highlight highlight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "imageUrl", column = @Column(name = "highlight_story_image_url")),
            @AttributeOverride(name = "imageType", column = @Column(name = "highlight_story_image_type")),
            @AttributeOverride(name = "imageName", column = @Column(name = "highlight_story_image_name")),
            @AttributeOverride(name = "imageUUID", column = @Column(name = "highlight_story_image_uuid"))
    })
    private Image image;

    @Builder
    public HighlightStory(Highlight highlight, Story story, Image image) {
        this.highlight = highlight;
        this.story = story;
        this.image = image;
    }
}
