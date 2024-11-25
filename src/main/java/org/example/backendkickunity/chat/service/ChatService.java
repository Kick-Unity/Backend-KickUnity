package org.example.backendkickunity.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.domain.MessageType;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
import org.example.backendkickunity.chat.repository.ChatMessageRepository;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방의 모든 메시지 조회
    public List<ChatMessageDTO> getChatMessages(ChatRoom chatRoom) {
        // 채팅방에 속한 모든 메시지를 최신순으로 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom);

        // 엔터티를 DTO로 변환하여 반환
        return chatMessages.stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 메시지 저장
    public ChatMessage saveChatMessage(ChatRoom chatRoom, String message, Member sender, MessageType messageType) {
        ChatMessage chatMessage = ChatMessage.builder()
                .message(message)
                .messageType(messageType)
                .chatRoom(chatRoom)
                .sender(sender)
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    // 채팅방 생성
    public ChatRoom createChatRoom(Member user1, Member user2) {
        // 채팅방 생성 로직
        ChatRoom chatRoom = ChatRoom.builder()
                .name(user1.getName() + " & " + user2.getName())
                .user1(user1)
                .user2(user2)
                .build();
        return chatRoomRepository.save(chatRoom);  // 채팅방 저장
    }
}
