package com.inhatc.cinebook.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {

	@NotNull(message = "별점을 입력해주세요.")
	@Min(value = 1, message = "별점을 선택해주세요.")
	@Max(value = 5, message = "별점은 5 이하여야 합니다.")
	private Double rating;

	@NotEmpty(message = "한줄평을 입력해주세요.")
	private String shortReview;

	@NotEmpty(message = "감상문을 입력해주세요.")
	private String reviewText;
}
