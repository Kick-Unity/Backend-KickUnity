package org.example.backendkickunity.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.global.entity.BaseEntity;
import org.example.backendkickunity.team.domain.Team;
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

    @Column(name = "birth", nullable = false)
    private String birth;

    @Column(name = "role")
    private String role;

    @ManyToOne
    private Team team;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards;  // 이 회원이 작성한 모든 게시물들

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY) // 이 회원이 참여한 모든 채팅방들
    private List<ChatRoom> chatRooms;
}
