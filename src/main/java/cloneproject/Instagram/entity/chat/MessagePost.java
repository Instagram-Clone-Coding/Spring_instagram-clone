package cloneproject.Instagram.entity.chat;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;
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
}
