package com.inhatc.cinebook.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByLoginId(loginId);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException("No such user.");
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		if ("admin".equals(loginId)) {
			authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
		} else {
			authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
		}

		return new org.springframework.security.core.userdetails.User(
				user.get().getLoginId(),
				user.get().getPassword(),
				authorities);
	}
}
