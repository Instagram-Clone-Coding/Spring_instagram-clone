package cloneproject.Instagram.domain.feed.service;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostLike;
import cloneproject.Instagram.domain.feed.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

	private final PostLikeRepository postLikeRepository;

	@Transactional
	public void deleteAll(Post post) {
		final List<PostLike> postLikes = postLikeRepository.findAllByPost(post);
		postLikeRepository.deleteAllInBatch(postLikes);
	}

}
