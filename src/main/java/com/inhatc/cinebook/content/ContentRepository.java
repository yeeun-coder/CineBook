package com.inhatc.cinebook.content;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContentRepository
		extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {

	Optional<Content> findByIsbnAndType(String isbn, ContentType type);
}
