package org.example.backendkickunity.member.domain;

public enum MemberRole {
    ADMIN, // 팀장
    USER; // 일반 사용자

    // toString을 오버라이드하여 enum을 String으로 자동 변환
    @Override
    public String toString() {
        return name().toLowerCase();  // 예: USER -> "user", ADMIN -> "admin"
    }
}
