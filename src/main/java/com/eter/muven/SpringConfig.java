package com.eter.muven;

import com.eter.auth.entity.UserAuditAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.eter.response.CommonResponseGenerator;

@Configuration
@EnableJpaAuditing
public class SpringConfig extends WebMvcConfigurerAdapter {
	
	Logger log = LoggerFactory.getLogger(SpringConfig.class);

	@Bean
	public CommonResponseGenerator commonResponseGenerator() {
		return new CommonResponseGenerator();
	}

	@Bean
	AuditorAware<String> auditorProvider() {
		return new UserAuditAware();
	}
}
