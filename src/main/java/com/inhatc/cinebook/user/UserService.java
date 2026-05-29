package com.inhatc.cinebook.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inhatc.cinebook.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User create(String loginId, String nickname, String password) {
		User user = new User();
		user.setLoginId(loginId);
		user.setNickname(nickname);
		user.setPassword(passwordEncoder.encode(password));
		return userRepository.save(user);
	}

	public User get(String loginId) {
		Optional<User> user = userRepository.findByLoginId(loginId);
		if (user.isPresent()) {
			return user.get();
		}
		throw new DataNotFoundException("User not found.");
	}

	public void updateNickname(User user, String nickname) {
		user.setNickname(nickname);
		userRepository.save(user);
	}
}
