package org.example.backendkickunity.chat.dto;

import lombok.*;
import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.MessageType;  // MessageType을 외부에서 가져옴
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private Long id; // 메시지 ID
    private String message; // 메시지 내용
    private MessageType messageType; // 메시지 타입
    private Long chatRoomId; // 채팅방 ID
    private Long senderId; // 발신자 ID
    private String senderName; // 발신자 이름
    private LocalDateTime createdAt; // 메시지 생성 시간

    // 엔터티를 DTO로 변환하는 메서드
    public static ChatMessageDTO fromEntity(ChatMessage chatMessage) {
        return ChatMessageDTO.builder()
                .id(chatMessage.getId())
                .message(chatMessage.getMessage())
                .messageType(chatMessage.getMessageType())  // MessageType 직접 할당
                .chatRoomId(chatMessage.getChatRoom().getId())
                .senderId(chatMessage.getSender().getId())
                .senderName(chatMessage.getSender().getName())
                .createdAt(chatMessage.getCreatedDate())
                .build();
    }
}
