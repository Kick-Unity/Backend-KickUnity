package org.example.backendkickunity.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    private Long chatRoomId;    // 채팅방 ID
    private Long senderId;      // 발신자 ID
    private String message;     // 메시지 내용
    private String messageType; // 메시지 타입 (ENUM 문자열)
}
