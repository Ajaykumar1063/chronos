package com.capstone.project.chronos.jobscheduling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SchedulerConfig {
    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(); // Simple TaskScheduler implementation
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

