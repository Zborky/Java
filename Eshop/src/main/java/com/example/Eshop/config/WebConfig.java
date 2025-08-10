package com.example.Eshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// The file is named WebConfig.java, which is fine.
// This @Configuration annotation is the important part.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use the absolute path to your uploads folder
        String absolutePath = "file:C:/Users/Jakub/Desktop/Java/Eshop/uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
