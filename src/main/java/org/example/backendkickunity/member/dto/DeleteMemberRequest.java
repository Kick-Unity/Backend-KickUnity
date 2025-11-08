package org.example.backendkickunity.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMemberRequest {

    private String password; // 회원 삭제 시 비밀번호 확인

}
