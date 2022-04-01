package cloneproject.Instagram.domain.feed.service;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.CommentLike;
import cloneproject.Instagram.domain.feed.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentLikeService {

	private final CommentLikeRepository commentLikeRepository;

	@Transactional
	public void deleteAll(List<Comment> comments) {
		final List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentIn(comments);
		commentLikeRepository.deleteAllInBatch(commentLikes);
	}
}
