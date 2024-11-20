package org.example.backendkickunity.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MypageResponse {

    private String teamName;
    private String email;
    private String name;
    private String birth;

}
