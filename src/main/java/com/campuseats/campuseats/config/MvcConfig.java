package com.campuseats.campuseats.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the external "uploads" directory to the web so images can be displayed
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}