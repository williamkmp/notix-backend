package com.william.notix.annotations.caller;

import com.william.notix.entities.User;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.KEY;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallerStompResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (
            parameter.hasParameterAnnotation(Caller.class) &&
            User.class.isAssignableFrom(parameter.getParameterType())
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
        Long senderId = Long.valueOf(
            header.getFirstNativeHeader(KEY.STOMP_HEADER_CALLER_USER_ID)
        );
        return userRepository.findById(senderId).orElse(null);
    }
}
