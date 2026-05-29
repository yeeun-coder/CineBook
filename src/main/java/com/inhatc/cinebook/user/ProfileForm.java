package com.inhatc.cinebook.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {

	@NotEmpty(message = "닉네임을 입력해주세요.")
	@Size(min = 2, max = 30, message = "닉네임은 {min}~{max}자 사이여야 합니다.")
	private String nickname;
}
