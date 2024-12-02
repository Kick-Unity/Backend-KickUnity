package org.example.backendkickunity.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.service.AuthService;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.exception.ChatException;
import org.example.backendkickunity.chat.exception.ChatExceptionType;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.member.exception.MemberExceptionType;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();  // ObjectMapper 초기화
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new ConcurrentHashMap<>();  // 채팅방 세션 관리
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final AuthService authService;  // 인증 서비스 추가
    private final MemberRepository memberRepository;

    // WebSocket 연결이 성공적으로 이루어졌을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Authorization 헤더에서 로그인된 사용자 이메일 추출
        String authHeader = session.getHandshakeHeaders().getFirst("Authorization");
        String email = authService.extractEmailFromAuthorizationHeader(authHeader); // 이메일 추출

        if (email == null) {
            // 이메일이 추출되지 않으면 인증 실패 처리
            session.close(CloseStatus.BAD_DATA);  // 연결 종료
            log.error("인증 실패: 유효하지 않은 토큰");
            return;
        }

        // 이메일을 통해 회원 정보 조회
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            session.close(CloseStatus.BAD_DATA);
            log.error("인증 실패: 유효하지 않은 사용자");
            return;
        }

        // 세션에 이메일과 이름 저장
        session.getAttributes().put("userEmail", email);
        session.getAttributes().put("userName", member.getName());  // 사용자 이름 추가

        // 세션에 채팅방 ID 저장
        Long chatRoomId = getChatRoomIdFromSession(session);
        session.getAttributes().put("chatRoomId", chatRoomId);

        // 채팅방에 세션 추가
        chatRoomSessionMap.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        // 연결된 사용자에게 확인 메시지 전송
        session.sendMessage(new TextMessage("채팅이 연결 되었어요!"));
        log.info("User {} connected to chat room {}", email, chatRoomId);
    }


    // WebSocket 연결이 끊어졌을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long chatRoomId = getChatRoomIdFromSession(session);

        // 채팅방에서 세션 제거
        Set<WebSocketSession> sessions = chatRoomSessionMap.get(chatRoomId);
        if (sessions != null) {
            sessions.remove(session);
        }

        String email = (String) session.getAttributes().get("userEmail");
        log.info("User {} disconnected from chat room {}", email, chatRoomId);
    }

    // 받은 메시지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // 메시지 처리 로직
        ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);

        // 이메일 추출 (세션에서 사용자 이메일을 가져옴)
        String email = (String) session.getAttributes().get("userEmail");
        if (email == null) {
            session.close(CloseStatus.BAD_DATA);
            log.error("인증 실패: 유효하지 않은 이메일");
            return;
        }

        // 채팅방을 데이터베이스에서 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoom().getId())
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_EXIST));

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }

        // 메시지를 해당 채팅방에 저장
        chatService.saveChatMessage(chatRoom, chatMessage.getMessage(), member, chatMessage.getMessageType());

        // 실시간 메시지 전송
        sendRealTimeMessage(chatRoom, chatMessage);
    }

    // 채팅방에 참여한 모든 세션에 실시간 메시지 전송
    public void sendRealTimeMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        // 채팅방에 참여한 모든 세션에 메시지 전송
        Set<WebSocketSession> sessions = chatRoomSessionMap.get(chatRoom.getId());
        if (sessions != null) {
            for (WebSocketSession webSocketSession : sessions) {
                if (webSocketSession.isOpen()) {
                    try {
                        // JSON 으로 변환하여 메시지 전송
                        String jsonMessage = mapper.writeValueAsString(chatMessage);
                        webSocketSession.sendMessage(new TextMessage(jsonMessage));
                    } catch (IOException e) {
                        log.error("WebSocket 메시지 전송 실패: {}, 세션 ID: {}", e.getMessage(), webSocketSession.getId());
                        // 예외가 발생한 세션을 채팅방에서 제거하고, 세션 종료 처리
                        sessions.remove(webSocketSession);
                    }
                }
            }
        }
    }

    // WebSocket 세션에서 채팅방 ID를 추출하는 방법
    private Long getChatRoomIdFromSession(WebSocketSession session) {
        // 세션에서 채팅방 ID를 추출하는 로직
        return (Long) session.getAttributes().get("chatRoomId");
    }

}
