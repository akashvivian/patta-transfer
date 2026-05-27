package com.example.pattatransfer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to enable Global CORS (Cross-Origin Resource Sharing).
 * This allows the HTML/CSS/JS frontend (running on a different port or directly from a local file)
 * to communicate seamlessly with the Spring Boot backend REST APIs.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow CORS on all endpoints
                .allowedOrigins(
                        "http://localhost:5500",
                        "http://127.0.0.1:5500",
                        "https://storied-heliotrope-0cd31e.netlify.app"
                ) // Explicitly permit Netlify production and local dev origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Allow all headers
                .maxAge(3600); // Cache CORS preflight response for 1 hour
    }
}
