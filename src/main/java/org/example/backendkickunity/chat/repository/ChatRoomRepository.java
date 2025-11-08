package org.example.backendkickunity.chat.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.example.backendkickunity.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 사용자가 참여한 채팅방 목록 조회
    List<ChatRoom> findByMembers(Member member);

    // 두 사용자가 포함된 채팅방 하나를 반환
    @Query("SELECT c FROM ChatRoom c WHERE :member1 MEMBER OF c.members AND :member2 MEMBER OF c.members")
    Optional<ChatRoom> findByMembersIn(@Param("member1") Member member1, @Param("member2") Member member2);
}

