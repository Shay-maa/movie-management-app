package com.example.demo;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootApplication
@EnableJpaAuditing


public class MoviesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesApplication.class, args);
	}

		@Bean
		@Transactional
		public CommandLineRunner runner(RoleRepository roleRepository) {
			return args -> {
				if (roleRepository.count() == 0) {
					Role userRole = new Role();
					userRole.setName("USER");

					Role adminRole = new Role();
					adminRole.setName("ADMIN");

					roleRepository.saveAll(List.of(userRole, adminRole));
				}
			};
}}