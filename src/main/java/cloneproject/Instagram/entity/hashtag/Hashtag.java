package cloneproject.Instagram.entity.hashtag;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column(name = "hashtag_name")
    private String name;

    @Column(name = "hashtag_count")
    private Integer count;

    @Builder
    public Hashtag(String name) {
        this.name = name;
        this.count = 1;
    }

    public void upCount() {
        this.count++;
    }

    public void downCount() {
        this.count--;
    }
}
