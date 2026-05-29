package com.inhatc.cinebook.content;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.inhatc.cinebook.DataNotFoundException;
import com.inhatc.cinebook.content.kakao.KakaoBookDocument;
import com.inhatc.cinebook.content.kakao.KakaoBookResponse;
import com.inhatc.cinebook.review.Review;
import com.inhatc.cinebook.user.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ContentService {

	private final ContentRepository contentRepository;
	private final RestTemplate restTemplate;
	private final com.inhatc.cinebook.review.ReviewRepository reviewRepository;

	@Value("${cinebook.kakao.api-key:}")
	private String kakaoApiKey;

	@Value("${cinebook.movie.api-key:}")
	private String movieApiKey;

	public Page<Content> getList(ContentType type, int page, String kw) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<Content> spec = search(type, kw);
		return contentRepository.findAll(spec, pageable);
	}

	public Content getContent(Long id) {
		Optional<Content> content = contentRepository.findById(id);
		if (content.isPresent()) {
			return content.get();
		}
		throw new DataNotFoundException("Content not found.");
	}

	public void create(String title, String creator, String imageUrl, ContentType type) {
		Content content = new Content();
		content.setTitle(title);
		content.setCreator(creator);
		content.setImageUrl(imageUrl);
		content.setType(type);
		content.setCreateDate(LocalDateTime.now());
		contentRepository.save(content);
	}

	public void modify(Content content, String title, String creator, String imageUrl) {
		content.setTitle(title);
		content.setCreator(creator);
		content.setImageUrl(imageUrl);
		contentRepository.save(content);
	}

	public void delete(Content content) {
		contentRepository.delete(content);
	}

	public double getAverageRating(Content content) {
		return reviewRepository.averageRatingByContentId(content.getId());
	}

	public int getReviewCount(Content content) {
		return (int) reviewRepository.countByContentId(content.getId());
	}

	public List<ContentCardView> toCardViews(List<Content> contents) {
		return contents.stream()
				.map(content -> new ContentCardView(
						content,
						getAverageRating(content),
						getReviewCount(content)))
				.toList();
	}

	public boolean isKakaoApiConfigured() {
		return kakaoApiKey != null && !kakaoApiKey.isBlank();
	}

	public List<BookSearchView> searchBooksFromKakao(String keyword, int page) {
		if (!isKakaoApiConfigured()) {
			return List.of();
		}
		if (keyword == null || keyword.isBlank()) {
			return List.of();
		}

		String url = UriComponentsBuilder
				.fromUriString("https://dapi.kakao.com/v3/search/book")
				.queryParam("query", keyword.trim())
				.queryParam("page", page + 1)
				.queryParam("size", 10)
				.build()
				.toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + kakaoApiKey);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<KakaoBookResponse> response = restTemplate.exchange(
					url, HttpMethod.GET, entity, KakaoBookResponse.class);
			KakaoBookResponse body = response.getBody();
			if (body == null || body.getDocuments() == null) {
				return List.of();
			}
			return body.getDocuments().stream()
					.map(this::toBookSearchView)
					.toList();
		} catch (RestClientException e) {
			throw new IllegalStateException("카카오 책 검색 API 호출에 실패했습니다.", e);
		}
	}

	public Content findOrCreateBook(String isbn, String title, String creator, String imageUrl) {
		String normalizedIsbn = normalizeIsbn(isbn);
		if (!normalizedIsbn.isBlank()) {
			Optional<Content> existing = contentRepository.findByIsbnAndType(
					normalizedIsbn, ContentType.BOOK);
			if (existing.isPresent()) {
				return existing.get();
			}
		}

		Content content = new Content();
		content.setIsbn(normalizedIsbn);
		content.setTitle(stripHtml(title));
		content.setCreator(creator);
		content.setImageUrl(
				imageUrl != null && !imageUrl.isBlank()
						? imageUrl
						: "https://via.placeholder.com/200x280?text=No+Cover");
		content.setType(ContentType.BOOK);
		content.setCreateDate(LocalDateTime.now());
		return contentRepository.save(content);
	}

	private BookSearchView toBookSearchView(KakaoBookDocument doc) {
		String authors = doc.getAuthors() == null || doc.getAuthors().isEmpty()
				? "저자 미상"
				: String.join(", ", doc.getAuthors());
		return new BookSearchView(
				normalizeIsbn(doc.getIsbn()),
				stripHtml(doc.getTitle()),
				authors,
				doc.getThumbnail(),
				doc.getPublisher());
	}

	private String normalizeIsbn(String isbn) {
		if (isbn == null || isbn.isBlank()) {
			return "";
		}
		return isbn.trim().split("\\s+")[0];
	}

	private String stripHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replaceAll("<[^>]*>", "").trim();
	}

	public void searchMovie(String keyword) {
		if (movieApiKey.isBlank()) {
			return;
		}
		String url = "https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp"
				+ "?collection=kmdb_new2"
				+ "&detail=Y"
				+ "&title=" + keyword
				+ "&ServiceKey=" + movieApiKey;
		String result = restTemplate.getForObject(url, String.class);
		System.out.println(result);
	}

	private Specification<Content> search(ContentType type, String kw) {
		return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			query.distinct(true);
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("type"), type));

			if (kw != null && !kw.isBlank()) {
				Join<Content, Review> reviewJoin = root.join("reviews", JoinType.LEFT);
				Join<Review, User> userJoin = reviewJoin.join("author", JoinType.LEFT);
				predicates.add(cb.or(
						cb.like(root.get("title"), "%" + kw + "%"),
						cb.like(root.get("creator"), "%" + kw + "%"),
						cb.like(reviewJoin.get("shortReview"), "%" + kw + "%"),
						cb.like(reviewJoin.get("reviewText"), "%" + kw + "%"),
						cb.like(userJoin.get("nickname"), "%" + kw + "%")));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
