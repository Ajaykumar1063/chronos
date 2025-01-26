package com.capstone.project.chronos.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
public class ApplicationConfiguration {

  @Autowired
  private AuthenticationProvider authenticationProvider;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(Customizer.withDefaults()) // Enable CORS with default settings
            .csrf(csrf -> csrf.disable()) // Disable CSRF if necessary
            .authorizeHttpRequests(authorizeRequests ->
                    authorizeRequests
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight requests
                            .requestMatchers("/api/user/register", "/api/user/verifyRegistration", "/api/user/login").permitAll() // Public endpoints
                            .anyRequest().authenticated() // Require authentication for other requests
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }



//  @Bean
//  public SecurityFilterChain securityFilterChainCors(HttpSecurity http) throws Exception {
//    http.cors().and() // Enable CORS support
//            .csrf().disable() // Disable CSRF for simplicity (consider enabling it in production)
//            .authorizeRequests()
//            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow OPTIONS preflight request
//            .anyRequest().authenticated(); // Secure all other requests
//
//    return http.build();
//  }
//
//  @Bean
//  public CorsFilter corsFilterChange() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    CorsConfiguration config = new CorsConfiguration();
//    config.setAllowCredentials(true);
//    config.addAllowedOrigin("http://localhost:3000"); // Add frontend origin
//    config.addAllowedHeader("*");
//    config.addAllowedMethod("*"); // Allow all HTTP methods
//    source.registerCorsConfiguration("/**", config);
//    return new CorsFilter(source);
//  }
}
