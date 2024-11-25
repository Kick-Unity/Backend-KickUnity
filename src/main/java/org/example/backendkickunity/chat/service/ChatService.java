package org.example.backendkickunity.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.repository.ChatMessageRepository;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoom createChatRoom(String roomName) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    // 메시지 저장
    @Transactional
    public ChatMessage saveChatMessage(ChatRoom chatRoom, String message, String sender, ChatMessage.MessageType messageType) {
        ChatMessage chatMessage = ChatMessage.builder()
                .message(message)
                .messageType(messageType)
                .sender(sender)
                .chatRoom(chatRoom)
                .build();
        return chatMessageRepository.save(chatMessage);
    }
}
