package cloneproject.Instagram.util;

import java.util.UUID;

import com.google.common.base.Enums;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.exception.NotSupportedImageTypeException;
import cloneproject.Instagram.vo.Image;
import cloneproject.Instagram.vo.ImageType;

public class ImageUtil {
    public static Image convertMultipartToImage(MultipartFile file){
        String url = "C:\\spring";
        String originalName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalName).toUpperCase();
        String fileName = FilenameUtils.getBaseName(originalName);

        if(!Enums.getIfPresent(ImageType.class, extension).isPresent())
            throw new NotSupportedImageTypeException();

        Image image = Image.builder()
                .imageUrl(url)
                .imageType(ImageType.valueOf(extension))
                .imageName(fileName)
                .imageUUID(UUID.randomUUID().toString())
                .build();
        return image;
    }
}
