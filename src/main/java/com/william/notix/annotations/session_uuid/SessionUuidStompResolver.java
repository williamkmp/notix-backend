package com.william.notix.annotations.session_uuid;

import com.william.notix.utils.values.KEY;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionUuidStompResolver implements HandlerMethodArgumentResolver {

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
        Message<?> message
    ) throws Exception {
        SimpMessageHeaderAccessor header = SimpMessageHeaderAccessor.wrap(
            message
        );
        return header.getFirstNativeHeader(
            KEY.STOMP_HEADER_CALLER_SESSION_UUID
        );
    }
}
