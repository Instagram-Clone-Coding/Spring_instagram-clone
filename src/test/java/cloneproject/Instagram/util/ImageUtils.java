package cloneproject.Instagram.util;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.global.vo.ImageType;

public class ImageUtils {

	public static Image newInstance() {
		final String name = RandomStringUtils.random(20, true, true);
		final ImageType type = ImageType.JPG;
		final String uuid = UUID.randomUUID().toString();
		final String url = RandomStringUtils.random(20, true, true);
		return of(name, type, uuid, url);
	}

	public static Image of(String name, ImageType type, String uuid, String url) {
		return Image.builder()
			.imageName(name)
			.imageType(type)
			.imageUUID(uuid)
			.imageUrl(url)
			.build();
	}

}
