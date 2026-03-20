package com.EcoSoftware.Scrum6.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:4200,https://ecosoftware.azurewebsites.net}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/tipoic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                // Configuracion anterior fija:
                // .setAllowedOrigins("https://ecosoftware.azurewebsites.net")
                .setAllowedOriginPatterns(splitAllowedOrigins())
                .withSockJS();
    }

    private String[] splitAllowedOrigins() {
        return java.util.Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }
}
