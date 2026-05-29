package com.inhatc.cinebook.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookSearchView {

	private final String isbn;
	private final String title;
	private final String creator;
	private final String imageUrl;
	private final String publisher;
}
