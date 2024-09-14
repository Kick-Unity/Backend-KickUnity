package org.example.backedkickunity.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Team {

    @OneToMany(mappedBy = "team")
    private List<User> users;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id", updatable = false)
    private Long id;

    @Column(name = "team_name", nullable = false, unique = true)
    private String teamName;
}
