package com.inhatc.cinebook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inhatc.cinebook.service.ContentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/movie/search")
    public String searchMovie(
            @RequestParam String keyword,
            Model model) {

        // API 호출
        contentService.searchMovie(keyword);

        return "movie/search";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {

        return "detail";
    }
}