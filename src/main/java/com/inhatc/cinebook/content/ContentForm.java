package com.inhatc.cinebook.content;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentForm {

	@NotEmpty(message = "제목을 입력해주세요.")
	@Size(max = 100)
	private String title;

	@NotEmpty(message = "저자/감독을 입력해주세요.")
	@Size(max = 100)
	private String creator;

	private String imageUrl;

	@NotNull(message = "유형을 선택해주세요.")
	private ContentType type;
}
