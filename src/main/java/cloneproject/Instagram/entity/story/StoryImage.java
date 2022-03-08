package cloneproject.Instagram.entity.story;

import cloneproject.Instagram.vo.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "story_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_image_id")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "imageUrl", column = @Column(name = "story_image_url")),
            @AttributeOverride(name = "imageType", column = @Column(name = "story_image_type")),
            @AttributeOverride(name = "imageName", column = @Column(name = "story_image_name")),
            @AttributeOverride(name = "imageUUID", column = @Column(name = "story_image_uuid"))
    })
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Builder
    public StoryImage(Image image, Story story) {
        this.image = image;
        this.story = story;
    }
}
