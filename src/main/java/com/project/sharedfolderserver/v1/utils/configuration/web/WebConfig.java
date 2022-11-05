package com.project.sharedfolderserver.v1.utils.configuration.web;

import com.project.sharedfolderserver.v1.utils.validation.json.ValidatedRequestBodyInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ValidatedRequestBodyInterceptor validatedRequestBodyInterceptor;
    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(validatedRequestBodyInterceptor);
    }
}