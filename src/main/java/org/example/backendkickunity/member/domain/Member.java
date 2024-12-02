package org.example.backendkickunity.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.global.entity.BaseEntity;
import org.example.backendkickunity.team.domain.Team;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class Member extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth")
    private String birth;

    @Column(name = "role")
    private String role;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true)
    private Team team;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Board> boards = new ArrayList<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatRoom> chatRooms = new ArrayList<>();
}
