package com.inhatc.cinebook.content;

import java.time.LocalDateTime;
import java.util.List;

import com.inhatc.cinebook.review.Review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Content {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private ContentType type;

	private String title;

	private String creator;

	private String imageUrl;

	/** 카카오 API ISBN (중복 등록 방지) */
	private String isbn;

	private LocalDateTime createDate;

	@OneToMany(mappedBy = "content", cascade = CascadeType.REMOVE)
	private List<Review> reviews;
}
