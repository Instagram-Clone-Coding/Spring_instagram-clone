package cloneproject.Instagram.entity.highlight;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "highlight_story_hashtags")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HighlightStoryHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlight_story_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highlight_story_id")
    private HighlightStory highlightStory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Builder
    public HighlightStoryHashtag(HighlightStory highlightStory, Hashtag hashtag) {
        this.highlightStory = highlightStory;
        this.hashtag = hashtag;
    }
}
