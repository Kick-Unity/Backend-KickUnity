package org.example.backendkickunity.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.global.entity.BaseEntity;
import org.example.backendkickunity.member.domain.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseEntity {

    // 채팅방에 속한 모든 사용자들
    @ManyToMany
    @JoinTable(
            name = "chat_room_member",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> members = new ArrayList<>();  // 사용자 목록

    // 채팅방 내의 모든 메시지들
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_room_id")  // 외래 키를 명시적으로 지정
    @Builder.Default
    private List<ChatMessage> chatMessages = new ArrayList<>();  // 채팅 메시지 목록
}
