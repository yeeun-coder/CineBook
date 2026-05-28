package com.inhatc.cinebook.controller;

import java.time.LocalDateTime;
import com.inhatc.cinebook.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inhatc.cinebook.entity.Review;

@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/write")
    public String writeForm() {

        return "review/write";
    }
    
    @PostMapping("/save")
    public String saveReview(Review review) {

        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        return "redirect:/";
    }
}
