package org.example.backendkickunity.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository; // 채팅방을 DB에서 조회하기 위해 필요

    private final Set<WebSocketSession> sessions = new HashSet<>();
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());
        sessions.add(session);
        session.sendMessage(new TextMessage("WebSocket 연결 완료"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        // 클라이언트로부터 받은 메세지를 ChatMessage 객체로 변환
        ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);

        // 채팅방 ID로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoom().getId())  // ChatMessage 객체에서 ChatRoom을 조회
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 메시지 저장
        chatService.saveChatMessage(chatRoom, chatMessage.getMessage(), chatMessage.getSender(), chatMessage.getMessageType());

        // 채팅방에 있는 모든 클라이언트에게 메시지 전송
        for (WebSocketSession webSocketSession : chatRoomSessionMap.get(chatRoom.getId())) {
            webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessage)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
        session.sendMessage(new TextMessage("WebSocket 연결 종료"));
    }
}
