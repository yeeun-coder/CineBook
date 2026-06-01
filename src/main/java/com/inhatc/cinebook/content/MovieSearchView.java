package com.inhatc.cinebook.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MovieSearchView {
	
	private final String title;
    private final String creator;
    private final String imageUrl;
}
