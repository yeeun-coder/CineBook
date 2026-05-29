package com.inhatc.cinebook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyMovieController {

	@GetMapping("/my-movie")
	public String myMovie() {
		return "redirect:/user/settings?filter=MOVIE";
	}

	@GetMapping("/my-library")
	public String myLibrary() {
		return "redirect:/user/settings?filter=BOOK";
	}
}
