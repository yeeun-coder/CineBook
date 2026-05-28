package com.inhatc.cinebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inhatc.cinebook.entity.Review;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

}