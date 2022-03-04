package cloneproject.Instagram.entity.post;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "hashtag_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashtagPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public HashtagPost(Hashtag hashtag, Post post) {
        this.hashtag = hashtag;
        this.post = post;
    }
}
