package com.william.notix.annotations.session_uuid;

import com.william.notix.utils.values.KEY;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class SessionUuidHttpResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (
            parameter.hasParameterAnnotation(SessionUuid.class) &&
            String.class.isAssignableFrom(parameter.getParameterType())
        );
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) throws Exception {
        return webRequest.getHeader(KEY.HTTP_HEADER_CALLER_SESSION_UUID);
    }
}
