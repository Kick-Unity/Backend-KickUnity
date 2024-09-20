package org.example.backedkickunity.domain;

import jakarta.persistence.*;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    String title;

    Category category;
}
