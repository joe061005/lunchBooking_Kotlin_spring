package com.project.lunchBooking.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


//@Configuration
//@EnableWebMvc
//class WebConfig : WebMvcConfigurer {
//    override fun addCorsMappings(corsRegistry: CorsRegistry) {
//        corsRegistry.addMapping("/**")
//            .allowedOrigins("http://localhost:4200")   //http://localhost:4200
//            .allowedMethods("*")
//            .maxAge(3600L)
//            .allowedHeaders("*")
//            .exposedHeaders("Authorization")
//            .allowCredentials(true)
//    }
//}