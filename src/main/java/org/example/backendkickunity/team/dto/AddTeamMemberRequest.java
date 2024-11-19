package org.example.backendkickunity.team.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddTeamMemberRequest {
    private String memberEmail;
}
