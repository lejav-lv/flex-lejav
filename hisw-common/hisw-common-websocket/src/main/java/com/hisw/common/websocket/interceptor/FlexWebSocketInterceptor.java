package com.hisw.common.websocket.interceptor;

import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.security.utils.LoginHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.hisw.common.websocket.constant.WebSocketConstants.LOGIN_USER_KEY;

/**
 * WebSocket握手请求的拦截器
 *
 * @author lejav
 */
@SuppressWarnings("all")
@Slf4j
public class FlexWebSocketInterceptor implements HandshakeInterceptor {

    /**
     * 握手前
     *
     * @param request    request
     * @param response   response
     * @param wsHandler  wsHandler
     * @param attributes attributes
     * @return 是否握手成功
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        attributes.put(LOGIN_USER_KEY, loginUser);
        return true;
    }

    /**
     * 握手后
     *
     * @param request   request
     * @param response  response
     * @param wsHandler wsHandler
     * @param exception 异常
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
