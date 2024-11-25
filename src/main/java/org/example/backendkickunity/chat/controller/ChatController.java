package org.example.backendkickunity.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.domain.MessageType;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 일대일 채팅방 생성
    @PostMapping("/create")
    public ChatRoom createChatRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Member user1 = memberRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User1이 존재하지 않습니다."));
        Member user2 = memberRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User2가 존재하지 않습니다."));

        return chatService.createChatRoom(user1, user2);
    }

    // 특정 채팅방의 메시지 기록 조회
    @GetMapping("/messages/{roomId}")
    public List<ChatMessageDTO> getChatMessages(@PathVariable Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        return chatService.getChatMessages(chatRoom);
    }

    // 메시지 전송 (실시간 WebSocket 메시지는 WebSocketChatHandler에서 처리)
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam Long roomId,
                                   @RequestParam String message,
                                   @RequestParam Long senderId,
                                   @RequestParam MessageType messageType) {
        // roomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // senderId로 사용자 조회
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 메시지 저장
        return chatService.saveChatMessage(chatRoom, message, sender, messageType);
    }
}
