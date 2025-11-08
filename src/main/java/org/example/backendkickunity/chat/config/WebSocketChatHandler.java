package org.example.backendkickunity.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.service.AuthService;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
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

    private final ObjectMapper mapper = new ObjectMapper();
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

        // 해당 채팅방에 세션을 추가
        chatRoomSessionMap.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        // 채팅방 이력 로드 (이 부분이 재접속한 사용자에게 이전 메시지를 전달하는 부분)
        loadChatRoomHistory(chatRoomId, session);

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

            // JSON 에서 필요한 값들 추출
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

            // 메시지 타입에 따라 처리
            switch (messageType) {
                case "JOIN":
                    handleJoin(chatRoomId, senderId, senderName);
                    break;

                case "TALK":
                    if (messageText == null || messageText.trim().isEmpty()) {
                        session.close(CloseStatus.BAD_DATA);
                        log.error("메시지가 비어있습니다.");
                        return;
                    }
                    handleTextMessageProcessing(chatRoomId, senderId, senderName, messageText);
                    break;

                case "LEAVE":
                    handleLeave(chatRoomId, senderId, senderName);
                    break;

                default:
                    session.close(CloseStatus.BAD_DATA);
                    log.error("유효하지 않은 메시지 타입입니다: {}", messageType);
                    break;
            }

        } catch (IOException e) {
            log.error("메시지 처리 오류: " + e.getMessage(), e);
            session.close(CloseStatus.BAD_DATA);
        }
    }

    // "JOIN" 메시지 처리
    private void handleJoin(Long chatRoomId, Long senderId, String senderName) throws IOException {
        // JOIN 처리는 클라이언트가 채팅방에 입장했음을 알리는 로직
        log.info("{}가 채팅방 {}에 입장했습니다.", senderName, chatRoomId);

        // 입장 알림 메시지 전송
        sendRealTimeMessage(chatRoomId, senderId, senderName, senderName + "님이 입장하셨습니다.");
    }

    // "TEXT" 메시지 처리
    private void handleTextMessageProcessing(Long chatRoomId, Long senderId, String senderName, String messageText) throws IOException {
        // 텍스트 메시지를 생성하고 저장하는 로직
        ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setChatRoomId(chatRoomId);
        chatMessageRequest.setSenderId(senderId);
        chatMessageRequest.setMessage(messageText);
        chatMessageRequest.setMessageType("TALK");

        // 메시지 생성 및 저장
        ChatMessage chatMessage = chatService.createChatMessage(chatMessageRequest);
        chatService.saveChatMessage(chatMessage);

        // 실시간 메시지 전송
        sendRealTimeMessage(chatRoomId, senderId, senderName, messageText);
    }

    // "LEAVE" 메시지 처리
    private void handleLeave(Long chatRoomId, Long senderId, String senderName) throws IOException {
        // LEAVE 처리는 사용자가 채팅방을 떠났다는 알림
        log.info("{}가 채팅방 {}에서 나갔습니다.", senderName, chatRoomId);

        // 나갔다는 메시지 전송
        sendRealTimeMessage(chatRoomId, senderId, senderName, senderName + "님이 나갔습니다.");

        // chatService 로 사용자 나가기 로직 위임
        chatService.handleUserExit(chatRoomId, senderId);
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

            String jsonString = mapper.writeValueAsString(jsonMessage);
            TextMessage realTimeMessage = new TextMessage(jsonString);

            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(realTimeMessage);
                } catch (IOException e) {
                    log.error("Failed to send message to session: {}", session.getId(), e);
                }
            }
        }
    }

    // 채팅방 이력 불러오기
    private void loadChatRoomHistory(Long chatRoomId, WebSocketSession session) throws IOException {
        // 기존 채팅 메시지들을 조회 (예: DB에서)
        List<ChatMessageDTO> chatMessages = chatService.getChatMessages(chatRoomId);

        // 기존 메시지들을 클라이언트에게 전송
        for (ChatMessageDTO chatMessage : chatMessages) {
            Map<String, Object> jsonMessage = new HashMap<>();
            jsonMessage.put("senderId", chatMessage.getSenderId());
            jsonMessage.put("senderName", chatMessage.getSenderName());
            jsonMessage.put("message", chatMessage.getMessage());
            jsonMessage.put("time", chatMessage.getTime());

            String jsonString = mapper.writeValueAsString(jsonMessage);
            session.sendMessage(new TextMessage(jsonString));  // 기존 메시지 전송
        }
    }

    // Long 으로 안전하게 변환하는 메서드
    private Long convertToLong(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        }
        throw new IllegalArgumentException("Invalid value type for conversion to Long: " + value);
    }

}
