package cloneproject.Instagram.global.vo;

import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

	@Lob
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	private ImageType imageType;

	private String imageName;

	private String imageUUID;

	public void setUrl(String url) {
		this.imageUrl = url;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getImageUUID());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Image image = (Image)obj;
		return Objects.equals(getImageUUID(), image.getImageUUID());
	}
}
