package org.example.backendkickunity.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteMemberRequest {

    private String password; // 회원 삭제 시 비밀번호 확인

}
