package com.capstone.project.chronos.jobscheduling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SchedulerConfig implements WebMvcConfigurer {
    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(); // Simple TaskScheduler implementation
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply this rule to all /** endpoints
                .allowedOrigins("http://localhost:3000") // Allow requests from localhost:3000
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific HTTP methods
                .allowedHeaders("*") // Allow any header
                .allowCredentials(true); // Allow credentials (cookies, HTTP authentication)
    }
}

