package com.inhatc.cinebook.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@GetMapping("/signin")
	public String signin() {
		return "signin_form";
	}

	@GetMapping("/signup")
	public String signup(UserForm userForm) {
		return "signup_form";
	}

	@PostMapping("/signup")
	public String signup(
			@Valid UserForm userForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}
		if (!userForm.getPassword1().equals(userForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordNotMatch", "비밀번호가 일치하지 않습니다.");
			return "signup_form";
		}
		try {
			userService.create(userForm.getLoginId(), userForm.getNickname(), userForm.getPassword1());
		} catch (DataIntegrityViolationException e) {
			bindingResult.reject("signupFailed", "이미 사용 중인 아이디 또는 닉네임입니다.");
			return "signup_form";
		}
		redirectAttributes.addFlashAttribute("signupSuccess", true);
		return "redirect:/user/signin";
	}
}
