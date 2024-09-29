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

    @Column(name = "email", nullable = false, unique = true)
    private String email; //unique key

    @Column(name = "password", nullable = false) //변경 가능
    private String password;

    @Column(name = "name", nullable = false) //변경 가능
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role; //ADMIN(팀장), USER(일반사용자, 팀가입자)

    @ManyToOne
    private Team team;

}
