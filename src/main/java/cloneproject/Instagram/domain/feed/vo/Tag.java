package cloneproject.Instagram.domain.feed.vo;

import java.util.Objects;

import javax.persistence.Embeddable;

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
public class Tag {

	private Double x;
	private Double y;
	private String username;

	@Override
	public int hashCode() {
		return Objects.hash(x, y, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Tag tag = (Tag)obj;
		return Objects.equals(x, tag.x) && Objects.equals(y, tag.y) && Objects.equals(username, tag.username);
	}

}
