package cloneproject.Instagram.entity.story;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "story_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_content_id")
    private Long id;

    @Lob
    @Column(name = "story_content_text")
    private String text;

    @Column(name = "story_content_y")
    private Long contentY;

    @Column(name = "story_content_x")
    private Long contentX;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_image_id")
    private StoryImage storyImage;

    @Builder
    public StoryContent(String text, Long contentY, Long contentX, StoryImage storyImage) {
        this.text = text;
        this.contentY = contentY;
        this.contentX = contentX;
        this.storyImage = storyImage;
    }
}
