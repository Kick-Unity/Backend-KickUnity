package org.example.backendkickunity.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private Long id; // 채팅방 ID
    private MemberDTO currentUser; // 로그인된 사용자 정보
    private MemberDTO otherUser;   // 상대방 정보

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberDTO {
        private Long id; // 사용자 ID
        private String name; // 사용자 이름
    }
}

