package org.example.backendkickunity.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.board.domain.Board;
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
    private String email; //unique key

    @Column(name = "password", nullable = false) //변경 가능
    private String password;

    @Column(name = "name", nullable = false) //닉네임 변경 가능
    private String name;

    @Column(name = "birth", nullable = false)
    private String birth;

    @Column(name = "role")
    private String role; //ADMIN(팀장), USER(일반사용자, 팀가입자)

    @ManyToOne
    private Team team;

    @OneToMany(mappedBy = "member") // Board 엔티티의 member 필드와 매핑
    private List<Board> boards;

}
