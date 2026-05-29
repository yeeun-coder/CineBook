package com.inhatc.cinebook.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContentCardView {

	private final Content content;
	private final double averageRating;
	private final long reviewCount;
}
