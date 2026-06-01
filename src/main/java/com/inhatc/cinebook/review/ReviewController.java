package com.inhatc.cinebook.review;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.inhatc.cinebook.content.BookSearchView;
import com.inhatc.cinebook.content.Content;
import com.inhatc.cinebook.content.ContentService;
import com.inhatc.cinebook.content.ContentType;
import com.inhatc.cinebook.user.User;
import com.inhatc.cinebook.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/review")
public class ReviewController {

	private final ReviewService reviewService;
	private final ReviewCommentService reviewCommentService;
	private final ContentService contentService;
	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
	public String writeForm(
			@RequestParam(name = "searchType", defaultValue = "BOOK") ContentType searchType,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "contentId", required = false) Long contentId,
			Model model,
			ReviewForm reviewForm) {

		model.addAttribute("searchType", searchType);
		model.addAttribute("keyword", keyword);

		if (contentId != null) {
			model.addAttribute("selectedContent", contentService.getContent(contentId));
		}

		if (!keyword.isBlank()) {
			loadSearchResults(searchType, keyword, model);
		}

		return "review/write";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write/select-book")
	public String selectBook(
			@RequestParam(name = "isbn") String isbn,
			@RequestParam(name = "title") String title,
			@RequestParam(name = "creator") String creator,
			@RequestParam(name = "imageUrl", required = false) String imageUrl) {
		Content content = contentService.findOrCreateBook(isbn, title, creator, imageUrl);
		return "redirect:/review/write?searchType=BOOK&contentId=" + content.getId();
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(
			@RequestParam(name = "contentId") Long contentId,
			@Valid ReviewForm reviewForm,
			BindingResult bindingResult,
			@RequestParam(name = "searchType", defaultValue = "BOOK") ContentType searchType,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			Model model,
			Principal principal) {

		Content content = contentService.getContent(contentId);

		if (bindingResult.hasErrors()) {
			model.addAttribute("selectedContent", content);
			model.addAttribute("searchType", searchType);
			model.addAttribute("keyword", keyword);
			if (!keyword.isBlank()) {
				loadSearchResults(searchType, keyword, model);
			}
			return "review/write";
		}

		User user = userService.get(principal.getName());
		reviewService.create(
				content,
				reviewForm.getRating(),
				reviewForm.getShortReview(),
				reviewForm.getReviewText(),
				user);
		return "redirect:/content/" + contentId;
	}

	@GetMapping("/{id}")
	public String detail(
			@PathVariable("id") Long id,
			Model model,
			CommentForm commentForm) {
		Review review = reviewService.get(id);
		model.addAttribute("review", review);
		model.addAttribute("comments", reviewCommentService.getByReview(id));
		model.addAttribute("commentCount", reviewCommentService.getCount(id));
		return "review/detail";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/comment")
	public String addComment(
			@PathVariable("id") Long id,
			@Valid CommentForm commentForm,
			BindingResult bindingResult,
			Principal principal) {
		if (bindingResult.hasErrors()) {
			return "redirect:/review/" + id + "?commentError=true";
		}
		Review review = reviewService.get(id);
		User user = userService.get(principal.getName());
		reviewCommentService.create(review, user, commentForm.getContent());
		return "redirect:/review/" + id;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/comment/delete/{commentId}")
	public String deleteComment(
			@PathVariable Long commentId,
			Principal principal) {
		Long reviewId = reviewCommentService.delete(commentId, principal.getName());
		return "redirect:/review/" + reviewId;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String modifyForm(
			ReviewForm reviewForm,
			@PathVariable("id") Long id,
			Model model,
			Principal principal) {
		Review review = reviewService.get(id);
		if (!review.getAuthor().getLoginId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		reviewForm.setRating(review.getRating());
		reviewForm.setShortReview(review.getShortReview());
		reviewForm.setReviewText(review.getReviewText());
		model.addAttribute("selectedContent", review.getContent());
		model.addAttribute("reviewId", id);
		model.addAttribute("searchType", review.getContent().getType());
		return "review/write";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modify(
			@Valid ReviewForm reviewForm,
			BindingResult bindingResult,
			@PathVariable("id") Long id,
			Model model,
			Principal principal) {
		Review review = reviewService.get(id);
		if (!review.getAuthor().getLoginId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("selectedContent", review.getContent());
			model.addAttribute("reviewId", id);
			model.addAttribute("searchType", review.getContent().getType());
			return "review/write";
		}
		reviewService.modify(
				review,
				reviewForm.getRating(),
				reviewForm.getShortReview(),
				reviewForm.getReviewText());
		return "redirect:/content/" + review.getContent().getId();
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id, Principal principal) {
		Review review = reviewService.get(id);
		if (!review.getAuthor().getLoginId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		Long contentId = review.getContent().getId();
		reviewService.delete(review);
		return "redirect:/content/" + contentId;
	}

	/** 하위 호환: 콘텐츠 상세에서 바로 진입 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write/{contentId}")
	public String writeFromContent(@PathVariable Long contentId) {
		Content content = contentService.getContent(contentId);
		return "redirect:/review/write?searchType=" + content.getType() + "&contentId=" + contentId;
	}

	private void loadSearchResults(ContentType searchType, String keyword, Model model) {
		if (searchType == ContentType.BOOK) {
			if (!contentService.isKakaoApiConfigured()) {
				model.addAttribute("apiError",
						"카카오 API 키가 없습니다. application.properties의 cinebook.kakao.api-key 를 설정해 주세요.");
				model.addAttribute("apiBooks", List.<BookSearchView>of());
			} else {
				try {
					model.addAttribute("apiBooks", contentService.searchBooksFromKakao(keyword, 0));
					model.addAttribute("apiError", null);
				} catch (IllegalStateException e) {
					model.addAttribute("apiBooks", List.<BookSearchView>of());
					model.addAttribute("apiError", e.getMessage());
				}
			}
		} else {
			var movies = contentService.getList(ContentType.MOVIE, 0, keyword).getContent();
			model.addAttribute("movieResults", movies);
		}
	}
}
