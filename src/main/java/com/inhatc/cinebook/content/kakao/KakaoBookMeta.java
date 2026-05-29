package com.inhatc.cinebook.content.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoBookMeta {

	private Integer total_count;
	private Boolean is_end;
}
