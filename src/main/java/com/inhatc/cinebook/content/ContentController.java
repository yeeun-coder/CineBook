package com.inhatc.cinebook.content;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inhatc.cinebook.review.Review;
import com.inhatc.cinebook.review.ReviewCommentService;
import com.inhatc.cinebook.review.ReviewForm;
import com.inhatc.cinebook.review.ReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/content")
public class ContentController {

	private final ContentService contentService;
	private final ReviewService reviewService;
	private final ReviewCommentService reviewCommentService;
	private final ContentRepository contentRepository;

	@GetMapping("/search")
	public String search(
			@RequestParam(name = "keyword") String keyword,
	        @RequestParam(name = "type", defaultValue = "MOVIE") ContentType type,
	        @RequestParam(name = "page", defaultValue = "0") int page,
			Model model) {

		if (type == ContentType.MOVIE) {

		    model.addAttribute("type", ContentType.MOVIE);
		    model.addAttribute("kw", keyword);
		    model.addAttribute("searchPage", page);

		    // KMDB API 검색
		    if (!contentService.isKmdbApiConfigured()) {

		        model.addAttribute("apiMovies", List.of());

		    } else {

		        try {

		            model.addAttribute(
		                    "apiMovies",
		                    contentService.searchMoviesFromTmdb(keyword, page));

		        } catch (Exception e) {

		            model.addAttribute("apiMovies", List.of());
		            model.addAttribute("apiError", e.getMessage());
		        }
		    }

		    var paging = contentService.getList(type, page, keyword);

		    model.addAttribute("paging", paging);

		    model.addAttribute(
		            "cards",
		            contentService.toCardViews(paging.getContent()));

		    return "movie";
		}

		return searchBooks(keyword, page, model);
	}

	private String searchBooks(String keyword, int page, Model model) {
		model.addAttribute("type", ContentType.BOOK);
		model.addAttribute("kw", keyword);
		model.addAttribute("searchPage", page);

		if (!contentService.isKakaoApiConfigured()) {
			model.addAttribute("apiError",
					"카카오 API 키가 없습니다. application.properties의 cinebook.kakao.api-key 를 설정해 주세요.");
			model.addAttribute("apiBooks", List.of());
		} else {
			try {
				model.addAttribute("apiBooks", contentService.searchBooksFromKakao(keyword, page));
				model.addAttribute("apiError", null);
			} catch (IllegalStateException e) {
				model.addAttribute("apiBooks", List.of());
				model.addAttribute("apiError", e.getMessage());
			}
		}

		var paging = contentService.getList(ContentType.BOOK, page, keyword);
		model.addAttribute("paging", paging);
		model.addAttribute("cards", contentService.toCardViews(paging.getContent()));
		return "book";
	}

	@PostMapping("/book/import")
	public String importBook(
			@RequestParam(name = "isbn") String isbn,
			@RequestParam(name = "title") String title,
			@RequestParam(name = "creator") String creator,
			@RequestParam(name = "imageUrl", required = false) String imageUrl,
			RedirectAttributes redirectAttributes) {
		try {
			Content content = contentService.findOrCreateBook(isbn, title, creator, imageUrl);
			redirectAttributes.addFlashAttribute(
			        "successMessage",
			        "책이 등록되었습니다. 리뷰를 작성해보세요.");

			return "redirect:/content/" + content.getId();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("importError", "책을 불러오지 못했습니다.");
			return "redirect:/book";
		}
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/movie/import")
	public String importMovie(
	        @RequestParam(name = "title") String title,
	        @RequestParam(name = "creator") String creator,
	        @RequestParam(name = "imageUrl", required = false) String imageUrl,
	        RedirectAttributes redirectAttributes) {

	    try {
	        Content content = contentService.findOrCreateMovie(title, creator, imageUrl);
	        redirectAttributes.addFlashAttribute(
	                "successMessage",
	                "영화가 등록되었습니다. 리뷰를 작성해보세요.");

	        return "redirect:/content/" + content.getId();
	    } catch (Exception e) {
//	        redirectAttributes.addFlashAttribute("importError", "영화를 불러오지 못했습니다.");
//	        return "redirect:/movie";
	    	System.out.println("title = " + title);
	    	System.out.println("creator = " + creator);
	    	System.out.println("imageUrl = " + imageUrl);
	    	e.printStackTrace();
	        throw e;
	    }
	}

	@GetMapping("/{id}")
	public String detail(
			@PathVariable("id") Long id,
			Model model,
			ReviewForm reviewForm) {
		Content content = contentService.getContent(id);
		List<Review> reviews = reviewService.getByContent(content);
		model.addAttribute("content", content);
		model.addAttribute("averageRating", contentService.getAverageRating(content));
		model.addAttribute("reviewCount", contentService.getReviewCount(content));
		model.addAttribute("reviews", reviews);
		model.addAttribute("commentCounts", reviewCommentService.getCountMap(reviews));
		return "detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/add")
	public String addForm(
	        ContentForm contentForm,
	        @RequestParam(name = "type") ContentType type,
	        @RequestParam(value = "keyword", required = false) String keyword,
	        Model model) {

	    contentForm.setType(type);

	    model.addAttribute("keyword", keyword);

	    if (keyword != null && !keyword.isBlank()) {

	        if (type == ContentType.MOVIE) {

	            model.addAttribute(
	                    "apiMovies",
	                    contentService.searchMoviesFromTmdb(keyword, 1));

	        } else {

	            model.addAttribute(
	                    "apiBooks",
	                    contentService.searchBooksFromKakao(keyword, 1));
	        }
	    }

	    return "content_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/add")
	public String add(
			@Valid ContentForm contentForm,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "content_form";
		}
		contentService.create(
				contentForm.getTitle(),
				contentForm.getCreator(),
				contentForm.getImageUrl(),
				contentForm.getType());
		if (contentForm.getType() == ContentType.MOVIE) {
			return "redirect:/movie";
		}
		return "redirect:/book";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String modifyForm(
			ContentForm contentForm,
			@PathVariable("id") Long id) {
		Content content = contentService.getContent(id);
		contentForm.setTitle(content.getTitle());
		contentForm.setCreator(content.getCreator());
		contentForm.setImageUrl(content.getImageUrl());
		contentForm.setType(content.getType());
		return "content_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modify(
			@Valid ContentForm contentForm,
			BindingResult bindingResult,
			@PathVariable("id") Long id) {
		if (bindingResult.hasErrors()) {
			return "content_form";
		}
		Content content = contentService.getContent(id);
		contentService.modify(
				content,
				contentForm.getTitle(),
				contentForm.getCreator(),
				contentForm.getImageUrl());
		return "redirect:/content/" + id;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		Content content = contentService.getContent(id);
		ContentType type = content.getType();
		contentService.delete(content);
		if (type == ContentType.MOVIE) {
			return "redirect:/movie";
		}
		return "redirect:/book";
	}
	
	@GetMapping("/exists")
	@ResponseBody
	public Map<String, Object> exists(
	        @RequestParam(name = "title") String title,
	        @RequestParam(name = "type") ContentType type){

	    Optional<Content> content =
	            contentRepository.findByTitleAndType(title, type);

	    Map<String, Object> result = new HashMap<>();

	    result.put("exists", content.isPresent());

	    if(content.isPresent()){
	        result.put("id", content.get().getId());
	    }

	    return result;
	}
}
