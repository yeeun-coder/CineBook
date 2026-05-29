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
public class BookController {

	private final ContentService contentService;

	@GetMapping("/book")
	public String book(
			Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		model.addAttribute("type", ContentType.BOOK);
		model.addAttribute("kw", kw);
		model.addAttribute("searchPage", page);

		if (!kw.isBlank()) {
			if (!contentService.isKakaoApiConfigured()) {
				model.addAttribute("apiError",
						"카카오 API 키가 없습니다. application.properties의 cinebook.kakao.api-key 를 설정해 주세요.");
				model.addAttribute("apiBooks", java.util.List.of());
			} else {
				try {
					model.addAttribute("apiBooks", contentService.searchBooksFromKakao(kw, page));
				} catch (IllegalStateException e) {
					model.addAttribute("apiBooks", java.util.List.of());
					model.addAttribute("apiError", e.getMessage());
				}
			}
		}

		var paging = contentService.getList(ContentType.BOOK, page, kw);
		model.addAttribute("paging", paging);
		model.addAttribute("cards", contentService.toCardViews(paging.getContent()));
		return "book";
	}
}
