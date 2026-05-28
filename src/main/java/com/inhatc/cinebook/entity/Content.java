package com.inhatc.cinebook.entity;

import jakarta.persistence.*;

@Entity
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    private String title;

    private String creator;

    private String imageUrl;
}