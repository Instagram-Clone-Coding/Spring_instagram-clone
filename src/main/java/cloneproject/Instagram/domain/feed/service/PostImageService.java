package cloneproject.Instagram.domain.feed.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.infra.aws.S3Uploader;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostImageService {

	private final S3Uploader uploader;
	private final PostImageRepository postImageRepository;
	private final PostTagService postTagService;

	@Transactional
	public void deleteAll(Post post) {
		final List<PostImage> postImages = postImageRepository.findAllByPost(post);
		postImages.forEach(pi -> uploader.deleteImage(pi.getImage(), "post"));
		postTagService.deleteAll(postImages);
		postImageRepository.deleteAllInBatch(postImages);
	}

	@Transactional
	public void saveAll(Post post, List<MultipartFile> multipartFiles, List<String> altTexts,
		List<PostImageTagRequest> tags) {
		final List<Image> images = multipartFiles.stream()
			.map(pi -> uploader.uploadImage(pi, "post"))
			.collect(Collectors.toList());

		postImageRepository.savePostImages(images, post.getId(), altTexts);
		for (int i = 0; i < images.size(); i++) {
			post.getPostImages().add(new PostImage(post, images.get(i), altTexts.get(i)));
		}

		if (!tags.isEmpty()) {
			linkWithTags(tags, post);
		}
		postTagService.saveAll(tags);
	}

	private void linkWithTags(List<PostImageTagRequest> postImageTags, Post post) {
		final List<Long> postImageIds = postImageRepository.findAllByPost(post).stream()
			.map(PostImage::getId)
			.collect(Collectors.toList());
		int idx = postImageTags.get(0).getId().intValue();

		for (PostImageTagRequest postImageTag : postImageTags) {
			if (idx != postImageTag.getId())
				idx = postImageTag.getId().intValue();
			postImageTag.setId(postImageIds.get(idx));
		}
	}

}
