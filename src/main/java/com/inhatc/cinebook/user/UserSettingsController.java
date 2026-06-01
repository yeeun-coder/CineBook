package com.inhatc.cinebook.user;

import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inhatc.cinebook.content.ContentType;
import com.inhatc.cinebook.review.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserSettingsController {

	private final UserService userService;
	private final ReviewService reviewService;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/settings")
	public String settings(
			Principal principal,
			@RequestParam(name = "filter", defaultValue = "ALL") String filter,
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        Model model) {
		User user = userService.get(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("reviewCount", reviewService.countByUser(user));
		model.addAttribute("totalStarsGiven", reviewService.sumRatingByUser(user));
		model.addAttribute("paging", reviewService.getByUserFiltered(user, filter, page));
		model.addAttribute("filter", filter);
		model.addAttribute("type", ContentType.MOVIE);
		model.addAttribute("kw", "");
		return "user/settings";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/settings/edit")
	public String editProfile(Principal principal, ProfileForm profileForm) {
		User user = userService.get(principal.getName());
		profileForm.setNickname(user.getNickname());
		return "user/settings_edit";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/settings/edit")
	public String editProfile(
			Principal principal,
			@Valid ProfileForm profileForm,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user/settings_edit";
		}
		User user = userService.get(principal.getName());
		try {
			userService.updateNickname(user, profileForm.getNickname());
		} catch (DataIntegrityViolationException e) {
			bindingResult.rejectValue("nickname", "duplicate", "이미 사용 중인 닉네임입니다.");
			return "user/settings_edit";
		}
		return "redirect:/user/settings";
	}
}
