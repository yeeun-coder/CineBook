package com.inhatc.cinebook.review;

import java.time.LocalDateTime;

import com.inhatc.cinebook.content.Content;
import com.inhatc.cinebook.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Content content;

	@ManyToOne
	private User author;

	private double rating;

	private String shortReview;

	@Column(length = 2000)
	private String reviewText;

	private LocalDateTime createdAt;

	private LocalDateTime modifyDate;
}
