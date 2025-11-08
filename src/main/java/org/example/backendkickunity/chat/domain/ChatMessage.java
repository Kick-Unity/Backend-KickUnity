package org.example.backendkickunity.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.global.entity.BaseEntity;
import org.example.backendkickunity.member.domain.Member;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    private String message;  // 메시지 내용

    @Enumerated(EnumType.STRING)
    private MessageType messageType;  // 메시지 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;  // 해당 메시지가 속한 채팅방

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;  // 메시지 발신자
}

