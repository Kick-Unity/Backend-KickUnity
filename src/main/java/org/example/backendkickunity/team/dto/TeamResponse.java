package org.example.backendkickunity.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {
    private String teamName;
    private String teamCategory;
    private String teamStartDate;
    private String teamRegion;
    private String teamAge;
    private int teamSize;
    private String teamDescription;
}
