package org.example.backendkickunity.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.service.AuthService;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.dto.ChatMessageRequest;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();  // Jackson을 여전히 사용
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final AuthService authService;
    private final MemberRepository memberRepository;

    // WebSocket 연결 시 사용자 정보와 채팅방 처리
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String authHeader = session.getHandshakeHeaders().getFirst("Authorization");
        String email = authService.extractEmailFromAuthorizationHeader(authHeader);

        if (email == null) {
            session.close(CloseStatus.BAD_DATA);
            log.error("인증 실패: 유효하지 않은 토큰");
            return;
        }

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            session.close(CloseStatus.BAD_DATA);
            log.error("인증 실패: 유효하지 않은 사용자");
            return;
        }

        // WebSocket URI에서 chatRoomId를 쿼리 파라미터로 추출
        String uri = session.getUri().toString();
        Map<String, String> queryParams = UriComponentsBuilder.fromUriString(uri).build().getQueryParams().toSingleValueMap();

        String chatRoomIdStr = queryParams.get("chatRoomId");

        if (chatRoomIdStr == null || chatRoomIdStr.isEmpty()) {
            log.error("chatRoomId가 null 또는 빈 문자열입니다.");
            session.close(CloseStatus.BAD_DATA);  // 클라이언트에게 잘못된 데이터로 연결 종료
            return;
        }

        Long chatRoomId;

        try {
            chatRoomId = Long.parseLong(chatRoomIdStr);
        } catch (NumberFormatException e) {
            log.error("chatRoomId가 유효하지 않습니다: {}", chatRoomIdStr);
            session.close(CloseStatus.BAD_DATA);  // 클라이언트에게 잘못된 데이터로 연결 종료
            return;
        }

        log.info("chatRoomId: " + chatRoomId);

        chatRoomSessionMap.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        session.sendMessage(new TextMessage("채팅이 연결 되었어요!"));
        log.info("User {} connected to chat room {}", email, chatRoomId);
    }

    // 클라이언트가 보낸 메시지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();  // 클라이언트가 보낸 메시지 내용

        try {
            // 클라이언트에서 보낸 JSON 메시지 파싱
            Map<String, Object> jsonMessage = mapper.readValue(payload, Map.class);

            // JSON에서 필요한 값들 추출
            Long chatRoomId = convertToLong(jsonMessage.get("chatRoomId"));
            String messageText = (String) jsonMessage.get("message");
            String messageType = (String) jsonMessage.get("messageType");
            Long senderId = convertToLong(jsonMessage.get("senderId"));

            // 채팅방 ID와 발신자 ID가 유효한지 체크
            if (chatRoomId == null || senderId == null || messageText == null || messageType == null) {
                session.close(CloseStatus.BAD_DATA);
                log.error("클라이언트에서 보낸 데이터가 유효하지 않습니다.");
                return;
            }

            // 발신자 이름을 senderId로부터 조회
            Member sender = memberRepository.findById(senderId).orElse(null);
            if (sender == null) {
                session.close(CloseStatus.BAD_DATA);
                log.error("sender 정보가 유효하지 않음");
                return;
            }

            String senderName = sender.getName();

            // ChatMessageRequest 객체 생성
            ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
            chatMessageRequest.setChatRoomId(chatRoomId);
            chatMessageRequest.setSenderId(senderId);
            chatMessageRequest.setMessage(messageText);
            chatMessageRequest.setMessageType(messageType);

            // 메시지 생성 및 저장
            ChatMessage chatMessage = chatService.createChatMessage(chatMessageRequest);
            chatService.saveChatMessage(chatMessage);

            // 실시간 메시지 전송
            sendRealTimeMessage(chatRoomId, senderId, senderName, messageText);

        } catch (IOException e) {
            log.error("메시지 처리 오류: " + e.getMessage(), e);
            session.close(CloseStatus.BAD_DATA);
        }
    }


    // 채팅방에 모든 세션에 실시간 메시지 전송
    public void sendRealTimeMessage(Long chatRoomId, Long senderId, String senderName, String messageText) throws IOException {
        Set<WebSocketSession> sessions = chatRoomSessionMap.get(chatRoomId);
        if (sessions != null) {
            // 메시지 전송 시간 (현재 시간)
            String currentTime = LocalDateTime.now().toString();

            // 객체 생성
            Map<String, Object> jsonMessage = new HashMap<>();
            jsonMessage.put("senderId", senderId);  // 발신자 이름
            jsonMessage.put("senderName", senderName);  // 발신자 이름
            jsonMessage.put("message", messageText);  // 메시지 내용
            jsonMessage.put("time", currentTime);  // 메시지 시간

            // ObjectMapper를 사용하여 객체를 JSON 문자열로 변환
            String jsonString = mapper.writeValueAsString(jsonMessage);

            // 생성된 JSON 메시지를 TextMessage로 변환하여 WebSocket 세션에 전송
            TextMessage realTimeMessage = new TextMessage(jsonString);
            for (WebSocketSession session : sessions) {
                session.sendMessage(realTimeMessage);
            }
        }
    }

    // Long 으로 안전하게 변환하는 메서드
    private Long convertToLong(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        }
        return null;  // 값을 변환할 수 없으면 null 반환
    }

}
