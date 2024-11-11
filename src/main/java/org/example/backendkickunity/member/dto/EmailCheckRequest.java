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
    private String password;   // 비밀번호
    private String name;       // 이름
    private String birth;       // 생년월일
    private String authNum;    // 인증번호

    // EmailCheckRequest에서 JoinRequest로 변환
    public JoinRequest toJoinRequest() {
        return new JoinRequest(this.email, this.password, this.name, this.birth);
    }
}
