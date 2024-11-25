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

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member user1;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member user2;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
