package org.example.backendkickunity.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamSummaryResponse {
    private Long id;
    private String teamName;
    private String teamCategory;
    private String teamRegion;
}
