package org.example.backendkickunity.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.chat.repository.ChatRoomRepository; // 추가된 부분
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository; // ChatRoomRepository 추가

    // 채팅방 생성
    @PostMapping("/create")
    public ChatRoom createChatRoom(@RequestParam String roomName) {
        return chatService.createChatRoom(roomName);
    }

    // 메시지 저장
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam Long roomId,
                                   @RequestParam String message,
                                   @RequestParam String sender,
                                   @RequestParam ChatMessage.MessageType messageType) {
        // roomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId) // DB에서 채팅방 조회
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 메시지 저장
        return chatService.saveChatMessage(chatRoom, message, sender, messageType);
    }
}
