package de.x1c1b.taskcare.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${taskcare.websocket.broker.host}")
    private String brokerHost;

    @Value("${taskcare.websocket.broker.port}")
    private int brokerPort;

    @Value("${taskcare.websocket.broker.username}")
    private String brokerUsername;

    @Value("${taskcare.websocket.broker.password}")
    private String brokerPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic")
                .setAutoStartup(true)
                .setRelayHost(brokerHost)
                .setRelayPort(brokerPort)
                .setSystemLogin(brokerUsername)
                .setSystemPasscode(brokerPassword)
                .setClientLogin(brokerUsername)
                .setClientPasscode(brokerPassword);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
