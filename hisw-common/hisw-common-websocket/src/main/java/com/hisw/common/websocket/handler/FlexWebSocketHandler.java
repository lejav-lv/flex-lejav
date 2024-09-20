package com.hisw.common.websocket.handler;

import cn.hutool.core.collection.ListUtil;
import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.websocket.constant.WebSocketConstants;
import com.hisw.common.websocket.dto.WebSocketMessageDto;
import com.hisw.common.websocket.holder.WebSocketSessionHolder;
import com.hisw.common.websocket.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.List;

/**
 * WebSocketHandler 实现类
 *
 * @author lejav
 */
@SuppressWarnings("all")
@Slf4j
public class FlexWebSocketHandler extends AbstractWebSocketHandler {

    /**
     * 连接成功后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LoginUser loginUser = (LoginUser) session.getAttributes().get(WebSocketConstants.LOGIN_USER_KEY);
        WebSocketSessionHolder.addSession(loginUser.getUserId(), session);
        log.info("[connect] sessionId: {},userId:{},userType:{}", session.getId(), loginUser.getUserId(), loginUser.getUserType());
    }

    /**
     * 处理发送来的文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        LoginUser loginUser = (LoginUser) session.getAttributes().get(WebSocketConstants.LOGIN_USER_KEY);
        List<Long> userIds = ListUtil.of(loginUser.getUserId());
        WebSocketMessageDto webSocketMessageDto = new WebSocketMessageDto();
        webSocketMessageDto.setSessionKeys(userIds);
        webSocketMessageDto.setMessage(message.getPayload());
        WebSocketUtils.publishMessage(webSocketMessageDto);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    /**
     * 心跳监测的回复
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        WebSocketUtils.sendPongMessage(session);
    }

    /**
     * 连接出错时
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("[transport error] sessionId: {} , exception:{}", session.getId(), exception.getMessage());
    }

    /**
     * 连接关闭后
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LoginUser loginUser = (LoginUser) session.getAttributes().get(WebSocketConstants.LOGIN_USER_KEY);
        WebSocketSessionHolder.removeSession(loginUser.getUserId());
        log.info("[disconnect] sessionId: {},userId:{},userType:{}", session.getId(), loginUser.getUserId(), loginUser.getUserType());
    }

    /**
     * 是否支持分片消息
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
