package org.example.backendkickunity.team.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.global.entity.BaseEntity;
import org.example.backendkickunity.member.domain.Member;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class Team extends BaseEntity {

    @Column(name = "team_name", nullable = false, unique = true)
    private String teamName;

    @Column(name = "team_category", nullable = false)
    private String teamCategory;

    @Column(name = "team_startdate", nullable = false)
    private String teamStartDate;

    @Column(name = "team_region", nullable = false)
    private String teamRegion;

    @Column(name = "team_age", nullable = false)
    private String teamAge;

    @Column(name = "team_size", nullable = false)
    private int teamSize;

    @ManyToOne
    @JoinColumn(name = "team_leader_id") // teamLeader_id 외래키 컬럼 설정
    private Member teamLeader; // 팀장

    @OneToMany(mappedBy = "team") // Member 엔터티의 team 필드와 매핑
    private List<Member> members;

}

