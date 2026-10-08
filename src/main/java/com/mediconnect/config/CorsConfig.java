package com.mediconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class CorsConfig {

    private final AuthenticatedUserInterceptor authenticatedUserInterceptor;

    public CorsConfig(AuthenticatedUserInterceptor authenticatedUserInterceptor) {
        this.authenticatedUserInterceptor = authenticatedUserInterceptor;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(authenticatedUserInterceptor)
                        .addPathPatterns(
                                "/api/medical-records",
                                "/api/medical-records/**",
                                "/api/users",
                                "/api/users/**"
                        );
            }
        };
    }
}
