package com.inhatc.cinebook.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inhatc.cinebook.content.ContentType;
import com.inhatc.cinebook.review.ReviewService;
import com.inhatc.cinebook.user.User;
import com.inhatc.cinebook.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MyMovieController {
	
	private final UserService userService;
    private final ReviewService reviewService;

    @GetMapping("/my-movie")
    public String myMovie(Principal principal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

	    User user = userService.get(principal.getName());

	    model.addAttribute(
	        "paging",
	        reviewService.getByUserFiltered(user, "MOVIE", page)
	    );

	    model.addAttribute("type", ContentType.MOVIE);

	    return "my-movie";
	}
//    @GetMapping("/my-movie")
//	public String myMovie() {
//		return "redirect:/user/settings?filter=MOVIE";
//	}
    
    @GetMapping("/my-library")
    public String myLibrary(Principal principal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

	    User user = userService.get(principal.getName());

	    model.addAttribute(
	        "paging",
	        reviewService.getByUserFiltered(user, "BOOK", page)
	    );

	    model.addAttribute("type", ContentType.MOVIE);

	    return "my-library";
	}
//	@GetMapping("/my-library")
//	public String myLibrary() {
//		return "redirect:/user/settings?filter=BOOK";
//	}
}
