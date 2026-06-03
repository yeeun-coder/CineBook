package com.inhatc.cinebook.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inhatc.cinebook.content.Content;
import com.inhatc.cinebook.content.ContentType;
import com.inhatc.cinebook.user.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByContentOrderByCreatedAtDesc(Content content);
//
//	Page<Review> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
	
	Page<Review> findByAuthor(
	        User author,
	        Pageable pageable);

	Page<Review> findByAuthorAndContent_Type(
	        User author,
	        ContentType type,
	        Pageable pageable);

//	Page<Review> findByAuthorAndContent_TypeOrderByCreatedAtDesc(
//			User author, ContentType type, Pageable pageable);

	long countByContentId(Long contentId);

	long countByAuthor(User author);

	@Query("SELECT COALESCE(SUM(r.rating), 0) FROM Review r WHERE r.author = :author")
	double sumRatingByAuthor(@Param("author") User author);

	@Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.content.id = :contentId")
	double averageRatingByContentId(@Param("contentId") Long contentId);
}
