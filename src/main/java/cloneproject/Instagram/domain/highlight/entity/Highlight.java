package cloneproject.Instagram.domain.highlight.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@Entity
@Table(name = "highlights")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Highlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlgiht_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "highlight_title")
    private String title;

    @Builder
    public Highlight(Member member, String title) {
        this.member = member;
        this.title = title;
    }
}
