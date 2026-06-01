package com.inhatc.cinebook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inhatc.cinebook.content.ContentService;
import com.inhatc.cinebook.content.ContentType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MovieController {

	private final ContentService contentService;

	@GetMapping("/movie")
	public String movie(
			Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		model.addAttribute("type", ContentType.MOVIE);
		model.addAttribute("kw", kw);
		model.addAttribute("searchPage", page);
		
		if (!kw.isBlank()) {
			if (!contentService.isKmdbApiConfigured()) {
				model.addAttribute("apiError",
						"영화 API 키가 없습니다. application.properties의 cinebook.movie.api-key 를 설정해 주세요.");
				model.addAttribute("apiMovies", java.util.List.of());
			} else {
				try {
					model.addAttribute("apiMovies", contentService.searchMoviesFromTmdb(kw, page));
				} catch (IllegalStateException e) {
					model.addAttribute("apiMovies", java.util.List.of());
					model.addAttribute("apiError", e.getMessage());
				}
			}
		}
		
		var paging = contentService.getList(ContentType.MOVIE, page, kw);
		model.addAttribute("paging", contentService.getList(ContentType.MOVIE, page, kw));
		model.addAttribute("cards", contentService.toCardViews(
				contentService.getList(ContentType.MOVIE, page, kw).getContent()));
		
		return "movie";
	}
}
