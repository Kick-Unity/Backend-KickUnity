package org.example.backendkickunity.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.domain.MessageType;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
import org.example.backendkickunity.chat.exception.ChatException;
import org.example.backendkickunity.chat.exception.ChatExceptionType;
import org.example.backendkickunity.chat.repository.ChatMessageRepository;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.member.exception.MemberExceptionType;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    private final ObjectMapper mapper = new ObjectMapper();  // ObjectMapper 초기화

    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new ConcurrentHashMap<>();  // 채팅방 세션 관리

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_EXIST));
    }

    // 채팅방에서 메시지 전송
    public ChatMessage saveChatMessage(ChatRoom chatRoom, String message, Member sender, MessageType messageType) {
        // 새로운 메시지 객체 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .message(message)
                .messageType(messageType)
                .chatRoom(chatRoom)
                .sender(sender)
                .build();

        // 메시지 저장
        return chatMessageRepository.save(chatMessage);
    }

    // 채팅방 ID로 채팅방 조회
    public ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_EXIST));
    }

    // 이메일로 회원 정보 조회
    public Member getMemberByEmail(String email) {
        Member findMember = memberRepository.findByEmail(email);
        if (findMember == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }
        return findMember;
    }

    // 채팅방의 모든 메시지 조회
    public List<ChatMessageDTO> getChatMessages(ChatRoom chatRoom) {
        // 채팅방에 속한 모든 메시지를 최신순으로 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom);

        // 엔터티를 DTO로 변환하여 반환
        return chatMessages.stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 채팅방 생성
    public ChatRoom createChatRoom(List<Member> members) {
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .members(members)  // user1과 user2를 리스트로 설정
                .build();

        // 채팅방을 저장하고 반환
        return chatRoomRepository.save(chatRoom);
    }

    // 채팅방에 참여한 모든 세션에 실시간 메시지 전송
    public void sendRealTimeMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        // 채팅방에 참여한 모든 세션에 메시지 전송
        Set<WebSocketSession> sessions = chatRoomSessionMap.get(chatRoom.getId());
        if (sessions != null) {
            for (WebSocketSession webSocketSession : sessions) {
                if (webSocketSession.isOpen()) {
                    try {
                        // JSON으로 변환하여 메시지 전송
                        String jsonMessage = mapper.writeValueAsString(chatMessage);
                        webSocketSession.sendMessage(new TextMessage(jsonMessage));
                    } catch (IOException e) {
                        log.error("WebSocket 메시지 전송 실패: {}", e.getMessage());
                    }
                }
            }
        }
    }
}
