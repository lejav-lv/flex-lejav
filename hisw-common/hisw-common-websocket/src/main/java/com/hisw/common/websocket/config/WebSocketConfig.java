package com.hisw.common.websocket.config;

import cn.hutool.core.util.StrUtil;
import com.hisw.common.websocket.config.properties.WebSocketProperties;
import com.hisw.common.websocket.handler.FlexWebSocketHandler;
import com.hisw.common.websocket.interceptor.FlexWebSocketInterceptor;
import com.hisw.common.websocket.listener.WebSocketTopicListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocket 配置
 *
 * @author lejav
 */
@AutoConfiguration
@ConditionalOnProperty(value = "websocket.enabled", havingValue = "true")
@EnableConfigurationProperties(WebSocketProperties.class)
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor handshakeInterceptor,
                                                   org.springframework.web.socket.WebSocketHandler webSocketHandler,
                                                   WebSocketProperties webSocketProperties) {
        if (StrUtil.isBlank(webSocketProperties.getPath())) {
            webSocketProperties.setPath("/websocket");
        }
        if (StrUtil.isBlank(webSocketProperties.getAllowedOrigins())) {
            webSocketProperties.setAllowedOrigins("*");
        }
        return registry -> registry
            .addHandler(webSocketHandler, webSocketProperties.getPath())
            .addInterceptors(handshakeInterceptor)
            .setAllowedOrigins(webSocketProperties.getAllowedOrigins());
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new FlexWebSocketInterceptor();
    }

    @Bean
    public org.springframework.web.socket.WebSocketHandler webSocketHandler() {
        return new FlexWebSocketHandler();
    }

    @Bean
    public WebSocketTopicListener topicListener() {
        return new WebSocketTopicListener();
    }
}
