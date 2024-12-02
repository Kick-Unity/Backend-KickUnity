package org.example.backendkickunity.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.exception.BoardException;
import org.example.backendkickunity.board.exception.BoardExceptionType;
import org.example.backendkickunity.board.repository.BoardRepository;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.chat.domain.MessageType;
import org.example.backendkickunity.chat.dto.ChatMessageDTO;
import org.example.backendkickunity.chat.dto.ChatMessageRequest;
import org.example.backendkickunity.chat.dto.ChatRoomDTO;
import org.example.backendkickunity.chat.exception.ChatException;
import org.example.backendkickunity.chat.exception.ChatExceptionType;
import org.example.backendkickunity.chat.repository.ChatMessageRepository;
import org.example.backendkickunity.chat.repository.ChatRoomRepository;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.member.exception.MemberExceptionType;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    private final ObjectMapper mapper = new ObjectMapper();  // ObjectMapper 초기화
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new ConcurrentHashMap<>();  // 채팅방 세션 관리


    // id로 회원 이름 조회
    @Transactional(readOnly = true)
    public String getMemberNameById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_EXIST));

        return member.getName();
    }

    // 이메일로 회원 정보 조회
    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        Member findMember = memberRepository.findByEmail(email);
        if (findMember == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }
        return findMember;
    }

    // ChatMessageRequest 를 사용하여 ChatMessage 생성
    public ChatMessage createChatMessage(ChatMessageRequest request) {
        // 채팅방 및 발신자 조회
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_EXIST));
        Member sender = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ChatException(ChatExceptionType.INVALID_SENDER));

        // 메시지 타입 변환
        MessageType messageType = MessageType.valueOf(request.getMessageType());

        // ChatMessage 엔티티 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .message(request.getMessage())
                .messageType(messageType)
                .chatRoom(chatRoom)
                .sender(sender)
                .build();

        return chatMessage;
    }

    // 채팅 메시지 저장
    @Transactional
    public void saveChatMessage(ChatMessage chatMessage) {
        // 메시지 저장
        chatMessageRepository.save(chatMessage);
    }

    // 채팅방 ID로 채팅방 조회
    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_EXIST));
    }

    // 채팅방의 모든 메시지 조회
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatMessages(Long chatRoomId) {
        // 채팅방 ID에 해당하는 메시지들을 조회 (DB나 다른 저장소에서)
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoomId);

        // DTO로 변환하여 반환
        List<ChatMessageDTO> chatMessageDTOs = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            ChatMessageDTO dto = new ChatMessageDTO(
                    chatMessage.getMessage(),
                    chatMessage.getSender().getId(),
                    chatMessage.getSender().getName(),
                    chatMessage.getCreatedDate().toString()
            );
            chatMessageDTOs.add(dto);
        }

        return chatMessageDTOs;
    }

    // 채팅방 생성
    @Transactional
    public ChatRoom createChatRoom(List<Member> members) {
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .members(members)  // user1과 user2를 리스트로 설정
                .build();

        // 채팅방을 저장하고 반환
        return chatRoomRepository.save(chatRoom);
    }

    // 게시글에서 작성자에게 채팅을 원할 때, 게시글 아이디를 통해 작성자 아이디 반환
    @Transactional(readOnly = true)
    public Member getAuthorByBoardId(Long boardId) {
        // 게시글을 boardId로 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardExceptionType.BOARD_NOT_EXIST));

        // 게시글의 작성자(Member) 반환
        return board.getMember();
    }

    public ChatRoom findExistingChatRoom(List<Member> members) {
        // 두 사용자가 속한 채팅방이 있는지 조회
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByMembersIn(members.get(0), members.get(1));

        // 채팅방이 존재하면 반환, 없으면 null
        return chatRoomOptional.orElse(null);
    }

    public List<ChatRoomDTO> getChatRoomsForUser(String email) {
        // 사용자를 이메일로 조회
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }

        // 사용자가 참여한 채팅방을 조회하고, DTO로 변환
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembers(member);
        return chatRooms.stream()
                .map(chatRoom -> {
                    // 현재 사용자의 정보와 상대방을 찾기 위한 메서드 사용
                    Member currentUser = findMemberByEmail(chatRoom, email);
                    Member otherUser = findOtherMember(chatRoom, email);

                    // 상대방이 나간 경우 처리
                    String otherUserName = otherUser != null ? otherUser.getName() : "상대방 없음";

                    // DTO로 변환하여 반환
                    return new ChatRoomDTO(
                            chatRoom.getId(),
                            new ChatRoomDTO.MemberDTO(currentUser.getId(), currentUser.getName()),
                            new ChatRoomDTO.MemberDTO(null, otherUserName)  // 상대방이 없으면 "상대방 없음"
                    );
                })
                .collect(Collectors.toList());
    }

    // 이메일로 사용자 찾기
    private Member findMemberByEmail(ChatRoom chatRoom, String email) {
        return chatRoom.getMembers().stream()
                .filter(m -> m.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_EXIST));
    }

    // 이메일로 상대방 찾기
    private Member findOtherMember(ChatRoom chatRoom, String email) {
        return chatRoom.getMembers().stream()
                .filter(m -> !m.getEmail().equals(email))
                .findFirst()
                .orElse(null); // 상대방이 나갔으면 null을  반환
    }


    // 채팅방 삭제
    public void deleteChatRoom(ChatRoom chatRoom) {
        // 해당 채팅방에 속한 메시지들을 삭제
        chatRoom.getChatMessages().forEach(chatMessage -> chatMessageRepository.delete(chatMessage));

        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }

    // 채팅방의 세션들 끊기
    public void disconnectChatRoomSessions(ChatRoom chatRoom) {
        // 해당 채팅방에 연결된 WebSocket 세션을 모두 종료
        Set<WebSocketSession> sessions = chatRoomSessionMap.get(chatRoom.getId());
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                try {
                    session.close(CloseStatus.NORMAL);
                } catch (IOException e) {
                    log.error("WebSocket 세션 종료 실패: {}", e.getMessage());
                }
            }
            chatRoomSessionMap.remove(chatRoom.getId()); // 세션 맵에서 채팅방 제거
        }
    }

    // 실시간 메시지 전송
    public void sendRealTimeMessageToClients(Long chatRoomId, ChatMessageDTO chatMessageDTO) {
        Set<WebSocketSession> sessions = chatRoomSessionMap.get(chatRoomId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessageDTO))); // 메시지 전송
                } catch (IOException e) {
                    log.error("WebSocket 메시지 전송 실패: {}", e.getMessage());
                }
            }
        }
    }

    // 사용자 나가기 처리
    public void handleUserExit(Long chatRoomId, Long exitingMemberId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_EXIST));
        Member exitingMember = memberRepository.findById(exitingMemberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_EXIST));

        // 채팅방에서 나간 사용자 제거
        chatRoom.getMembers().remove(exitingMember);

        // 만약 두 명 모두 나갔다면 채팅방 삭제
        if (chatRoom.getMembers().isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        } else {
            // 한 명이 나갔고, 다른 사용자는 남아있다면 해당 상태 업데이트
            // 예: 나간 사용자 상태 변경 (나간 사용자에게 '나갔습니다' 메시지 등 표시)
            ChatMessage exitMessage = new ChatMessage();
            exitMessage.setSender(exitingMember);
            exitMessage.setMessage(exitingMember.getName() + "님이 나갔습니다.");
            exitMessage.setChatRoom(chatRoom);

            chatRoom.getChatMessages().add(exitMessage);  // "나갔습니다" 메시지 추가

            // 채팅방 상태 업데이트
            chatRoomRepository.save(chatRoom);
        }
    }
}
