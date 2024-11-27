package org.example.backendkickunity.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.domain.MessageType;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
import org.example.backendkickunity.chat.repository.ChatMessageRepository;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + id));
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
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
    }

    // 이메일로 회원 정보 조회
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
               // .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
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


}
