package com.inhatc.cinebook.content.kakao;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KmdbMovieResponse {
	
	private KmdbMovieMeta meta;
	private List<KmdbMovieDocument> documents = new ArrayList<>();
}	
