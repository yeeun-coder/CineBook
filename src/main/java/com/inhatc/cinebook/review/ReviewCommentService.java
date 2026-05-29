package com.inhatc.cinebook.review;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.inhatc.cinebook.DataNotFoundException;
import com.inhatc.cinebook.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewCommentService {

	private final ReviewCommentRepository reviewCommentRepository;

	public List<ReviewComment> getByReview(Long reviewId) {
		return reviewCommentRepository.findByReviewIdOrderByCreatedAtAsc(reviewId);
	}

	public long getCount(Long reviewId) {
		return reviewCommentRepository.countByReviewId(reviewId);
	}

	public Map<Long, Long> getCountMap(List<Review> reviews) {
		Map<Long, Long> map = new HashMap<>();
		for (Review review : reviews) {
			map.put(review.getId(), reviewCommentRepository.countByReviewId(review.getId()));
		}
		return map;
	}

	public void create(Review review, User author, String content) {
		ReviewComment comment = new ReviewComment();
		comment.setReview(review);
		comment.setAuthor(author);
		comment.setContent(content);
		comment.setCreatedAt(LocalDateTime.now());
		reviewCommentRepository.save(comment);
	}

	public ReviewComment get(Long id) {
		Optional<ReviewComment> comment = reviewCommentRepository.findById(id);
		if (comment.isPresent()) {
			return comment.get();
		}
		throw new DataNotFoundException("Comment not found.");
	}

	public Long delete(Long commentId, String loginId) {
		ReviewComment comment = get(commentId);
		if (!comment.getAuthor().getLoginId().equals(loginId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		Long reviewId = comment.getReview().getId();
		reviewCommentRepository.delete(comment);
		return reviewId;
	}
}
