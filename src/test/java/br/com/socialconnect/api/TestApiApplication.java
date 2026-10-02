package br.com.socialconnect.api;

import org.springframework.boot.SpringApplication;

public class TestApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ApiApplication::main)
				.run("--spring.profiles.active=test");
	}

}
