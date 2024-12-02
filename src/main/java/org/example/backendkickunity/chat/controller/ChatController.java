package org.example.backendkickunity.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.service.AuthService;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
import org.example.backendkickunity.chat.dto.ChatRoomDTO;
import org.example.backendkickunity.chat.exception.ChatException;
import org.example.backendkickunity.chat.exception.ChatExceptionType;
import org.example.backendkickunity.chat.service.ChatService;
import org.example.backendkickunity.member.domain.Member;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final AuthService authService;

    // 채팅방 생성 API
    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestParam Long boardId) {
        // Authorization 헤더에서 로그인된 사용자 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        Member currentUser = chatService.getMemberByEmail(email);

        // 현재 로그인한 사용자가 채팅을 원하는 게시글 작성자 멤버 조회
        Member otherUser = chatService.getAuthorByBoardId(boardId);

        // user1과 user2를 리스트에 담기
        List<Member> members = new ArrayList<>();
        members.add(currentUser);
        members.add(otherUser);

        // 채팅방 생성
        ChatRoom savedChatRoom = chatService.createChatRoom(members);  // 수정된 서비스 호출

        // ChatRoomDTO 로 변환
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
        // 채팅방 정보 가져오기
        ChatRoom chatRoom = chatService.getChatRoomById(roomId);

        // 해당 채팅방의 메시지를 DTO 형식으로 반환 (송신자 이름, 시간 포함)
        return chatService.getChatMessages(chatRoom);
    }


    // 채팅방 삭제 API
    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<String> deleteChatRoom(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                 @PathVariable Long roomId) {
        // Authorization 헤더에서 로그인된 사용자 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        Member member = chatService.getMemberByEmail(email);  // 로그인된 사용자 정보

        // 채팅방 조회
        ChatRoom chatRoom = chatService.getChatRoomById(roomId);

        // 채팅방에 참여한 사용자가 아니면 삭제 권한이 없음
        if (!chatRoom.getMembers().contains(member)) {
            throw new ChatException(ChatExceptionType.NOT_MEMBER_OF_CHATROOM);
        }

        // 채팅방 삭제
        chatService.deleteChatRoom(chatRoom);

        // 채팅방 삭제 후, WebSocket 에서 해당 채팅방에 연결된 모든 세션 종료
        chatService.disconnectChatRoomSessions(chatRoom);

        return ResponseEntity.ok("채팅방이 삭제되었습니다.");
    }

    public void sendMessageToChatRoom(Long chatRoomId, Long senderId, String message) {
        String senderName = chatService.getMemberNameById(senderId);  // senderId로 이름 조회
        String time = java.time.LocalDateTime.now().toString();  // 현재 시간 설정

        // 클라이언트가 예상하는 형식으로 메시지 객체 생성
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(message, senderId, senderName, time);

        chatService.sendRealTimeMessageToClients(chatRoomId, chatMessageDTO);
    }

    // 사용자가 참여한 채팅방 목록을 조회하는 API
    @GetMapping("/chatRooms")
    public List<ChatRoomDTO> getChatRoomsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // Authorization 헤더에서 로그인된 사용자 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // 조회된 채팅방 목록을 반환
        return chatService.getChatRoomsForUser(email);
    }




}
