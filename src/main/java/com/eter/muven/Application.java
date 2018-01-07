package com.eter.muven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.eter.response.annotation.EnableLoadResponse;

@Configuration
@EnableAutoConfiguration
@EntityScan({"com.eter.cake.persistence.entity","com.eter.cake.persistence.entity.rest"})
@EnableJpaRepositories({"com.eter.cake.persistence.repo", "com.eter.cake.persistence.service"})
@ComponentScan({"com.eter.muven.cake.controller"})
@Import({SpringConfig.class, DaoSpringConfig.class})
@EnableLoadResponse
public class Application {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}
