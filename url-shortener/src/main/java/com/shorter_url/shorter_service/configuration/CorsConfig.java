package com.shorter_url.shorter_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/* CORS - cross-origin resource sharing
Механизм безопасности браузера.
Origin = protocol + domain + port http://localhost:8080

fetch() -
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/api/**") // применить CORS только к URL, начинающимся с /api/
                        .allowedOrigins("*") // любой сайт может обращаться к API

                        .allowedMethods("GET", "POST", "DELETE");
            }
        };
    }

}
