package org.example.backendkickunity.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailCheckRequest {
    private String email;      // 이메일
    private String authNum;    // 인증번호
}
