package com.capstone.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication
public class ChronosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChronosApplication.class, args);
	}

//	@Bean
//	public Validator validator() {
//		return new LocalValidatorFactoryBean();
//	}
}
