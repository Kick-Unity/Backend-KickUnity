package org.example.backendkickunity.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backendkickunity.global.entity.BaseEntity;
import org.example.backendkickunity.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public Board(String title, String content, Member member, BoardCategory category) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.category = category;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}
