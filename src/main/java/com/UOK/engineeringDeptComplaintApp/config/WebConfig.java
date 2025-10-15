package com.UOK.engineeringDeptComplaintApp.config; // Use your actual config package

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map any request starting with /images/ to the physical file system directory
        registry.addResourceHandler("/images/**")
                // IMPORTANT: The "file:" prefix tells Spring to look directly on the disk
                .addResourceLocations("file:" + uploadDirectory + "/");
    }
}