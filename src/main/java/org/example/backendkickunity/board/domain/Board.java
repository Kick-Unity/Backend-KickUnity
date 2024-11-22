package org.example.backendkickunity.board.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendkickunity.global.entity.BaseEntity;
import org.example.backendkickunity.member.domain.Member;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 기본 생성자는 비공개로 설정
@NoArgsConstructor
@Builder  // Builder 패턴 추가
public class Board extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardCategory category;  // 운동 종목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false) // Member 테이블의 id와 연결, 작성자 member_id
    private Member member;

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}
