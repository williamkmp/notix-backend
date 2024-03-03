package com.william.notix.configurations;

import com.william.notix.annotations.caller.CallerHttpResolver;
import com.william.notix.annotations.session_uuid.SessionUuidHttpResolver;
import com.william.notix.interceptors.AuthInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final CallerHttpResolver callerHttpResolver;
    private final SessionUuidHttpResolver sessionUuidHttpResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }

    @Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(callerHttpResolver);
        resolvers.add(sessionUuidHttpResolver);
    }
}
