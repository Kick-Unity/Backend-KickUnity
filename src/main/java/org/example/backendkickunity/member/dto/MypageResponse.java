package org.example.backendkickunity.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MypageResponse {

    private String team;
    private String email;
    private String name;
    private String birth;

    public MypageResponse() {}

}
