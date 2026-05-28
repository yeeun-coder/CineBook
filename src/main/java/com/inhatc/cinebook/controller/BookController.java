package com.inhatc.cinebook.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class BookController {
	@GetMapping("/book")
    public String book() {

        return "book";
    }
}
