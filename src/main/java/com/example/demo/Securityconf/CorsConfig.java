package com.example.demo.Securityconf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class CorsConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL}")
    private String frontendUrl;
    public void addCorsMappings(CorsRegistry registry) {
         registry.addMapping("/**")
                 .allowedOrigins(frontendUrl)
                 .allowedMethods("GET", "POST", "PUT", "DELETE")
                 .allowedHeaders("*")
                 .allowCredentials(true);
   }
}
