package com.inhatc.cinebook.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Content content;

    // 별점
    private double rating;

    // 한줄평
    private String shortReview;

    // 감상문
    @Column(length = 2000)
    private String reviewText;

    // 작성일
    private LocalDateTime createdAt;
}