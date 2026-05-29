package com.inhatc.cinebook.review;

import java.time.LocalDateTime;

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
public class ReviewComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Review review;

	@ManyToOne
	private User author;

	@Column(length = 500)
	private String content;

	private LocalDateTime createdAt;
}
