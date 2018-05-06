package com.mitchtalmadge.cryptex.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Add a resource handler for all non-api URLs.
        // If the URL begins with "/api/" or is exactly "/api", it will not be handled as a resource.
        registry.addResourceHandler("/{root:^(?!api).*}/**", "/{root:^api.+}/**").addResourceLocations("classpath:/static/");
    }
}
