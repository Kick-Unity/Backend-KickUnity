package org.example.backendkickunity.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.dto.ChatRoomDTO;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;

    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());

        // URL에서 채팅방 ID 추출
        String path = session.getUri().getPath();
        Long chatRoomId = extractChatRoomId(path);

        // 채팅방 ID가 없으면 새 채팅방 생성
        if (chatRoomId == null) {
            // 채팅방이 없으면 URL 경로에서 사용자 ID 추출
            String[] segments = path.split("/");
            Long user1Id = Long.valueOf(segments[segments.length - 2]);
            Long user2Id = Long.valueOf(segments[segments.length - 1]);

            Member user1 = chatService.getMemberById(user1Id);
            Member user2 = chatService.getMemberById(user2Id);

            // 채팅방 생성 (List<Member> 사용)
            List<Member> members = new ArrayList<>();
            members.add(user1);
            members.add(user2);

            // 채팅방 생성 및 저장
            ChatRoom chatRoom = chatService.createChatRoom(members);  // 수정된 부분

            // 새로운 채팅방 ID로 채팅방 정보 저장
            chatRoomId = chatRoom.getId();
        }

        // 채팅방에 사용자 추가
        chatRoomSessionMap.computeIfAbsent(chatRoomId, k -> new HashSet<>()).add(session);

        // 채팅방에 참여자가 2명 이상인 경우 처리
        if (chatRoomSessionMap.get(chatRoomId).size() > 2) {
            session.sendMessage(new TextMessage("채팅방에 2명 이상 참여할 수 없습니다."));
            session.close();
            return;
        }

        log.info("채팅방 {}에 사용자 {} 연결됨", chatRoomId, session.getId());
        session.sendMessage(new TextMessage("WebSocket 연결 완료"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("받은 메시지: {}", payload);

        // 클라이언트에서 받은 메시지를 ChatMessage 객체로 변환
        ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 메시지 저장
        chatService.saveChatMessage(chatRoom, chatMessage.getMessage(), chatMessage.getSender(), chatMessage.getMessageType());

        // 해당 채팅방에 참여한 모든 세션에 메시지 전송
        for (WebSocketSession webSocketSession : chatRoomSessionMap.get(chatRoom.getId())) {
            webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessage)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());

        // 연결 끊어진 세션을 관리 맵에서 제거
        chatRoomSessionMap.values().forEach(sessions -> sessions.remove(session));

        session.sendMessage(new TextMessage("WebSocket 연결 종료"));
    }

    // 채팅방 ID 추출
    private Long extractChatRoomId(String path) {
        try {
            String[] segments = path.split("/");
            return Long.valueOf(segments[segments.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }
}
