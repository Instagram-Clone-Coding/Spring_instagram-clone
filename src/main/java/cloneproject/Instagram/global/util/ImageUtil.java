package cloneproject.Instagram.global.util;

import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Enums;

import cloneproject.Instagram.global.error.exception.NotSupportedImageTypeException;
import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.global.vo.ImageType;

public class ImageUtil {

	public static Image convertMultipartToImage(MultipartFile file) {

		final String originalName = file.getOriginalFilename();
		final String name = FilenameUtils.getBaseName(originalName);
		final String type = FilenameUtils.getExtension(originalName).toUpperCase();

		if (!Enums.getIfPresent(ImageType.class, type).isPresent()) {
			throw new NotSupportedImageTypeException();
		}

		return Image.builder()
			.imageType(ImageType.valueOf(type))
			.imageName(name)
			.imageUUID(UUID.randomUUID().toString())
			.build();
	}

	public static Image getBaseImage() {
		return Image.builder()
			.imageName("base")
			.imageType(ImageType.PNG)
			.imageUrl("https://instagram-s3-dev.s3.ap-northeast-2.amazonaws.com/member/base-UUID_base.PNG.png")
			.imageUUID("base-UUID")
			.build();
	}

}
