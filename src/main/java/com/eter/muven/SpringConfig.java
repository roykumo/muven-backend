package com.eter.muven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.eter.response.CommonResponseGenerator;

@Configuration
public class SpringConfig extends WebMvcConfigurerAdapter {
	
	Logger log = LoggerFactory.getLogger(SpringConfig.class);

	@Bean
	public CommonResponseGenerator commonResponseGenerator() {
		return new CommonResponseGenerator();
	}
}
