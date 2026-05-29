package com.inhatc.cinebook;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.inhatc.cinebook.content.Content;
import com.inhatc.cinebook.content.ContentRepository;
import com.inhatc.cinebook.content.ContentType;
import com.inhatc.cinebook.user.User;
import com.inhatc.cinebook.user.UserRepository;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner init(
			ContentRepository contentRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() == 0) {
				User admin = new User();
				admin.setLoginId("admin");
				admin.setNickname("관리자");
				admin.setPassword(passwordEncoder.encode("admin"));
				userRepository.save(admin);

				User user = new User();
				user.setLoginId("user");
				user.setNickname("독서광인티티");
				user.setPassword(passwordEncoder.encode("user"));
				userRepository.save(user);
			}

			if (contentRepository.count() == 0) {
				Content interstellar = new Content();
				interstellar.setType(ContentType.MOVIE);
				interstellar.setTitle("인터스텔라");
				interstellar.setCreator("크리스토퍼 놀란");
				interstellar.setImageUrl("https://i.imgur.com/8Km9tLL.jpg");
				interstellar.setCreateDate(LocalDateTime.now());
				contentRepository.save(interstellar);

				Content summer = new Content();
				summer.setType(ContentType.BOOK);
				summer.setTitle("바깥은 여름");
				summer.setCreator("김애란");
				summer.setImageUrl("https://image.yes24.com/goods/119242056/XL");
				summer.setCreateDate(LocalDateTime.now());
				contentRepository.save(summer);

				Content lalaland = new Content();
				lalaland.setType(ContentType.MOVIE);
				lalaland.setTitle("라라랜드");
				lalaland.setCreator("데미안 샤젤");
				lalaland.setImageUrl("https://i.imgur.com/7D7I6dI.jpg");
				lalaland.setCreateDate(LocalDateTime.now());
				contentRepository.save(lalaland);
			}
		};
	}
}
