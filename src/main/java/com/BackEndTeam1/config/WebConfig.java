package com.BackEndTeam1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // /upload/**로 요청이 들어오면 실제 파일 시스템 경로에 있는 파일을 서빙
//        registry.addResourceHandler("/upload/**")
//                .addResourceLocations("file:./src/main/resources/static/upload/");  // 실제 파일 경로로 수정
//    }
}
