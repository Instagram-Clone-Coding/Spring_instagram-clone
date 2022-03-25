package cloneproject.Instagram.domain.dm.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("TEXT")
@Table(name = "message_texts")
public class MessageText extends Message {

    @Lob
    @Column(name = "message_text_content")
    private String content;

    public MessageText(String content, Member member, Room room) {
        super(member, room);
        this.content = content;
    }
}
