package org.example.backendkickunity.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.config.domain.BaseEntity;
import org.example.backendkickunity.domain.Team;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id; //primary key

    @Column(name = "member_id", nullable = false, unique = true)
    private String memberId; //unique key

    @Column(name = "password", nullable = false) //변경 가능
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email; //unique key

    @Column(name = "name", nullable = false) //변경 가능
    private String name;

    private boolean deleted = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    private Role role; //ADMIN(팀장), USER(일반사용자, 팀가입자)

    @Column(name = "team_id")
    private Long teamIndex = null;

    @ManyToOne
    private Team team;

    public void delete() {
        memberId = null;
        password = null;
        email = null;
        name = null;
        deleted = true;
    }
}
