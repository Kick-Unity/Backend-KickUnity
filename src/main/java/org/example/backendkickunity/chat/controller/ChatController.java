package org.example.backendkickunity.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.service.AuthService;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.domain.MessageType;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
import org.example.backendkickunity.chat.dto.ChatRoomDTO;
import org.example.backendkickunity.chat.exception.ChatException;
import org.example.backendkickunity.chat.exception.ChatExceptionType;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestParam Long user2Id) {
        // Authorization 헤더에서 로그인된 사용자 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        Member user1 = chatService.getMemberByEmail(email);

        // user2 조회
        Member user2 = chatService.getMemberById(user2Id);

        // user1과 user2를 리스트에 담기
        List<Member> members = new ArrayList<>();
        members.add(user1);
        members.add(user2);

        // 채팅방 생성
        ChatRoom savedChatRoom = chatService.createChatRoom(members);  // 수정된 서비스 호출

        // ChatRoomDTO로 변환
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(
                savedChatRoom.getId(),
                new ChatRoomDTO.MemberDTO(savedChatRoom.getMembers().get(0).getId(), savedChatRoom.getMembers().get(0).getName()),  // currentUser
                new ChatRoomDTO.MemberDTO(savedChatRoom.getMembers().get(1).getId(), savedChatRoom.getMembers().get(1).getName())   // otherUser
        );

        return ResponseEntity.ok(chatRoomDTO);  // 생성된 채팅방 DTO 반환
    }


    // 특정 채팅방의 메시지 기록 조회
    @GetMapping("/messages/{roomId}")
    public List<ChatMessageDTO> getChatMessages(@PathVariable Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_EXIST));
        return chatService.getChatMessages(chatRoom);
    }

    // 메시지 전송 (HTTP API에서 메시지 전송 후, WebSocket을 통해 실시간 처리)
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestParam Long roomId,
                                                      @RequestParam String message,
                                                      @RequestParam Long senderId,
                                                      @RequestParam MessageType messageType) {
        // Authorization 헤더에서 로그인된 사용자 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        Member sender = chatService.getMemberByEmail(email);  // 로그인된 사용자 정보

        // roomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_EXIST));

        // senderId로 사용자 조회 (이 값은 프론트에서 보내준 실제 발신자의 ID와 일치해야 함)
        if (!sender.getId().equals(senderId)) {
            throw new ChatException(ChatExceptionType.INVALID_SENDER);
        }

        // 메시지 저장
        ChatMessage chatMessage = chatService.saveChatMessage(chatRoom, message, sender, messageType);

        // 저장된 메시지를 실시간으로 전송
        chatService.sendRealTimeMessage(chatRoom, chatMessage);

        // 저장된 메시지를 DTO로 변환하여 반환
        return ResponseEntity.ok(ChatMessageDTO.fromEntity(chatMessage));  // 생성된 메시지 DTO 반환
    }

}
