package cloneproject.Instagram.domain.dm.entity;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("POST")
@Table(name = "message_posts")
public class MessagePost extends Message {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public MessagePost(Post post, Member member, Room room) {
        super(member, room);
        this.post = post;
    }

    public void deletePost() {
        this.post = null;
    }

}
