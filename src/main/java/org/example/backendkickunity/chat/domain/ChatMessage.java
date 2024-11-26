package org.example.backendkickunity.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.global.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    private String message;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String sender;

    public enum MessageType {
        JOIN, TALK, LEAVE
    }
}
