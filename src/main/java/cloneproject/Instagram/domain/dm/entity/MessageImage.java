package cloneproject.Instagram.domain.dm.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.global.vo.Image;

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
