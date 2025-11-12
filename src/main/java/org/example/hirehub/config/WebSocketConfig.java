package org.example.hirehub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // from server to client
        config.enableSimpleBroker("/topic", "/queue"); //topic: public/group msg; queue: private msg

        // from client to server
        config.setApplicationDestinationPrefixes("/app"); // public/group msg
        config.setUserDestinationPrefix("/user"); //private
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        //define endpoint to websocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // allow all domain
                .withSockJS(); // allow SockJS fallback
    }
}