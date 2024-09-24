package org.example.backedkickunity.domain;

import jakarta.persistence.*;
import org.example.backedkickunity.domain.member.Member;

import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name = "team_id", nullable = false, unique = true)
    private String teamId;

    @Column(name = "team_name", nullable = false, unique = true)
    private String teamName;

    @OneToMany(mappedBy = "team")
    private List<Member> members;
}
