package com.inhatc.cinebook.user;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
	        @RequestParam(name = "sort", defaultValue = "latest") String sort,
	        Model model) {
		User user = userService.get(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("reviewCount", reviewService.countByUser(user));
		model.addAttribute("totalStarsGiven", reviewService.sumRatingByUser(user));
		model.addAttribute("paging", reviewService.getByUserFiltered(user, filter, sort, page));
		model.addAttribute("filter", filter);
		model.addAttribute("sort", sort);
		model.addAttribute("type", ContentType.MOVIE);
		model.addAttribute("kw", "");
		return "user/settings";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/settings/edit")
	public String editProfile(Principal principal, ProfileForm profileForm, Model model) {
		User user = userService.get(principal.getName());
		profileForm.setNickname(user.getNickname());
		model.addAttribute("user", user);
		return "user/settings_edit";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/settings/edit")
	public String editProfile(Principal principal,
			@Valid ProfileForm profileForm, BindingResult bindingResult,
			@RequestParam(name = "deleteImage", defaultValue = "false") String deleteImage) {
		if (bindingResult.hasErrors()) {
			return "user/settings_edit";
		}
		User user = userService.get(principal.getName());
		try {

		    user.setNickname(profileForm.getNickname());

		    MultipartFile image = profileForm.getProfileImage();

		    if (image != null && !image.isEmpty()) {

		        String fileName =
		                UUID.randomUUID() + "_" + image.getOriginalFilename();

		        Path uploadPath = Paths.get("uploads");

		        if (!Files.exists(uploadPath)) {
		            Files.createDirectories(uploadPath);
		        }

		        image.transferTo(uploadPath.resolve(fileName));

		        user.setProfileImage(fileName);
		    }
		    if (deleteImage.equals("true")) {
		        user.setProfileImage("default-profile.png");
		    }
		    userService.save(user);

		} catch (DataIntegrityViolationException e) {

		    bindingResult.rejectValue(
		            "nickname",
		            "duplicate",
		            "이미 사용 중인 닉네임입니다.");

		    return "user/settings_edit";

		} catch (Exception e) {

		    e.printStackTrace();

		    bindingResult.reject(
		            "uploadError",
		            "이미지 업로드 중 오류가 발생했습니다.");

		    return "user/settings_edit";
		}
		return "redirect:/user/settings";
	}
}
