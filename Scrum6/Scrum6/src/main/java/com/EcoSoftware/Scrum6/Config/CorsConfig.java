package com.EcoSoftware.Scrum6.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,https://ecosoftware.azurewebsites.net}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // Configuracion anterior fija:
                        // .allowedOrigins("https://ecosoftware.azurewebsites.net")
                        .allowedOriginPatterns(splitAllowedOrigins())
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .maxAge(3600)
                        .allowCredentials(true);
            }
        };
    }

    private String[] splitAllowedOrigins() {
        return java.util.Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }
}
