package com.william.notix.configurations;

import com.google.gson.Gson;
import com.william.notix.annotations.caller.CallerStompResolver;
import com.william.notix.annotations.session_uuid.SessionUuidStompResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.GsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class SocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final Gson gson;
    private final CallerStompResolver callerStompResolver;
    private final SessionUuidStompResolver sessionUuidStompResolver;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(
        StompEndpointRegistry stompEndpointRegistry
    ) {
        stompEndpointRegistry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    @Override
    public boolean configureMessageConverters(
        List<MessageConverter> messageConverters
    ) {
        GsonMessageConverter messageConverter = new GsonMessageConverter(gson);
        messageConverters.add(messageConverter);
        return false;
    }

    @Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> resolvers
    ) {
        WebSocketMessageBrokerConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(callerStompResolver);
        resolvers.add(sessionUuidStompResolver);
    }
}
