package cloneproject.Instagram.entity.hashtag;

import cloneproject.Instagram.entity.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "hashtags")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column(name = "hashtag_name")
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "hashtag_count")
    private Integer count;

    @Builder
    public Hashtag(String name, Post post) {
        this.name = name;
        this.post = post;
        this.count = 1;
    }

    public void upCount() {
        this.count++;
    }

    public void downCount() {
        this.count--;
    }
}
