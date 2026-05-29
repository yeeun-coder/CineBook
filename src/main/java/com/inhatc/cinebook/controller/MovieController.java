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
		model.addAttribute("paging", contentService.getList(ContentType.MOVIE, page, kw));
		model.addAttribute("cards", contentService.toCardViews(
				contentService.getList(ContentType.MOVIE, page, kw).getContent()));
		model.addAttribute("kw", kw);
		model.addAttribute("type", ContentType.MOVIE);
		return "movie";
	}
}
