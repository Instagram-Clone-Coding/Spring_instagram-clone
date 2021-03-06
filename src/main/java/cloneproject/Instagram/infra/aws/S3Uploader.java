package cloneproject.Instagram.infra.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.global.error.exception.CantConvertFileException;
import cloneproject.Instagram.global.util.ImageUtil;
import cloneproject.Instagram.global.vo.Image;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	public String bucket;

	public String upload(MultipartFile multipartFile, String dirName, String UUID, String name, String type)
		throws IOException {
		final File uploadFile = convert(multipartFile)
			.orElseThrow(CantConvertFileException::new);

		return upload(uploadFile, dirName, UUID, name, type);
	}

	public Image uploadImage(MultipartFile multipartFile, String dirName) {
		try {
			final Image image = ImageUtil.convertMultipartToImage(multipartFile);
			final String url = upload(multipartFile, dirName, image.getImageUUID(),
				image.getImageName(), image.getImageType().toString());
			image.setUrl(url);
			return image;
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void delete(String dirName, String UUID, String name) {
		final String filename = dirName + "/" + UUID + "_" + name;
		deleteS3(filename);
	}

	public void deleteImage(String dirName, Image image) {
		if (image.getImageUUID().equals("base-UUID")) {
			return;
		}
		final String filename = dirName + "/" + image.getImageUUID() + "_" + image.getImageName()
			+ "." + image.getImageType().toString();
		deleteS3(filename);
	}

	private String upload(File uploadFile, String dirName, String UUID, String name, String type) {
		final String fileName = dirName + "/" + UUID + "_" + name + "." + type;
		final String uploadImageUrl = putS3(uploadFile, fileName);
		removeNewFile(uploadFile);
		return uploadImageUrl;
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
			new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	private void deleteS3(String filename) {
		amazonS3Client.deleteObject(bucket, filename);
	}

	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			return;
		}
		log.info("File delete fail");
	}

	// ????????? ?????? ????????? ??????
	private Optional<File> convert(MultipartFile file) throws IOException {
		final File convertFile = new File(System.getProperty("user.dir") + "\\upload\\" + file.getOriginalFilename());
		if (convertFile.createNewFile()) { // ?????? ????????? ????????? ????????? File??? ????????? (????????? ?????????????????? ?????? ?????????)
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				// FileOutputStream ???????????? ????????? ????????? ??????????????? ???????????? ??????
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}

		return Optional.empty();
	}

}
