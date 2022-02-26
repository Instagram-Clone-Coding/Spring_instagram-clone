package cloneproject.Instagram.entity.chat;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.vo.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("IMAGE")
@Table(name = "message_images")
public class MessageImage extends Message {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "imageUrl", column = @Column(name = "message_image_url")),
            @AttributeOverride(name = "imageType", column = @Column(name = "message_image_type")),
            @AttributeOverride(name = "imageName", column = @Column(name = "message_image_name")),
            @AttributeOverride(name = "imageUUID", column = @Column(name = "message_image_uuid"))
    })
    private Image image;

    public MessageImage(Image image, Member member, Room room) {
        super(member, room);
        this.image = image;
    }
}
