package com.inhatc.cinebook.content.kakao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoBookDocument {

	private String title;
	private String isbn;
	private List<String> authors;
	private String thumbnail;
	private String publisher;
}
