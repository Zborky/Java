package com.kasino;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.kasino.repository.UserRepository;

@SpringBootApplication
public class KasinoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KasinoApplication.class, args);
	}

	@Bean
	public CommandLineRunner makeMeAdmin(UserRepository userRepository) {
		return args -> {
			String mojeMeno = "JakubZboron"; // ← sem daj svoje meno
			userRepository.findByUsername(mojeMeno).ifPresent(user -> {
				user.setRole("ADMIN");
				userRepository.save(user);
				System.out.println("Používateľ '" + mojeMeno + "' bol nastavený ako ADMIN.");
			});
		};
	}
}
