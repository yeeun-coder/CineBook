package com.inhatc.cinebook.review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

	@NotEmpty(message = "댓글을 입력해주세요.")
	@Size(max = 500, message = "댓글은 500자 이하로 입력해주세요.")
	private String content;
}
