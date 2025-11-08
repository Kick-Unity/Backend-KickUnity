package org.example.backendkickunity.chat.dto;

import lombok.*;
import org.example.backendkickunity.chat.domain.ChatMessage;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private String message; // 메시지 내용
    private Long senderId;
    private String senderName; // 발신자 이름
    private String time; // 메시지 생성 시간

    // 엔터티를 DTO로 변환하는 메서드
    public static ChatMessageDTO fromEntity(ChatMessage chatMessage) {
        return ChatMessageDTO.builder()
                .message(chatMessage.getMessage())
                .senderId(chatMessage.getSender().getId())
                .senderName(chatMessage.getSender().getName())
                .time(String.valueOf(chatMessage.getCreatedDate()))
                .build();
    }
}