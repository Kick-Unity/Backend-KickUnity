package org.example.backendkickunity.chat.repository;

import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 사용자가 참여한 채팅방 목록 조회
    List<ChatRoom> findByMembers(Member member);
}
