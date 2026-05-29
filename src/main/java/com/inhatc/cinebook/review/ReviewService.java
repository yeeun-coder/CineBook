package com.inhatc.cinebook.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.inhatc.cinebook.DataNotFoundException;
import com.inhatc.cinebook.content.Content;
import com.inhatc.cinebook.content.ContentType;
import com.inhatc.cinebook.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewCommentRepository reviewCommentRepository;

	public List<Review> getByContent(Content content) {
		return reviewRepository.findByContentOrderByCreatedAtDesc(content);
	}

	public Page<Review> getByUser(User user, ContentType type, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createdAt"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return reviewRepository.findByAuthorAndContent_TypeOrderByCreatedAtDesc(user, type, pageable);
	}

	public Page<Review> getByUserFiltered(User user, String filter, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createdAt"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		if ("MOVIE".equals(filter)) {
			return reviewRepository.findByAuthorAndContent_TypeOrderByCreatedAtDesc(
					user, ContentType.MOVIE, pageable);
		}
		if ("BOOK".equals(filter)) {
			return reviewRepository.findByAuthorAndContent_TypeOrderByCreatedAtDesc(
					user, ContentType.BOOK, pageable);
		}
		return reviewRepository.findByAuthorOrderByCreatedAtDesc(user, pageable);
	}

	public long countByUser(User user) {
		return reviewRepository.countByAuthor(user);
	}

	public double sumRatingByUser(User user) {
		return reviewRepository.sumRatingByAuthor(user);
	}

	public Review get(Long id) {
		Optional<Review> review = reviewRepository.findById(id);
		if (review.isPresent()) {
			return review.get();
		}
		throw new DataNotFoundException("Review not found.");
	}

	public void create(
			Content content,
			double rating,
			String shortReview,
			String reviewText,
			User author) {
		Review review = new Review();
		review.setContent(content);
		review.setRating(rating);
		review.setShortReview(shortReview);
		review.setReviewText(reviewText);
		review.setAuthor(author);
		review.setCreatedAt(LocalDateTime.now());
		reviewRepository.save(review);
	}

	public void modify(Review review, double rating, String shortReview, String reviewText) {
		review.setRating(rating);
		review.setShortReview(shortReview);
		review.setReviewText(reviewText);
		review.setModifyDate(LocalDateTime.now());
		reviewRepository.save(review);
	}

	public void delete(Review review) {
		reviewRepository.delete(review);
	}

	public Map<Long, Long> getCommentCountMap(List<Review> reviews) {
		Map<Long, Long> map = new HashMap<>();
		for (Review review : reviews) {
			map.put(review.getId(), reviewCommentRepository.countByReviewId(review.getId()));
		}
		return map;
	}
}
