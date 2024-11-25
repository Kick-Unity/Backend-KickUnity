package org.example.backendkickunity.chat.repository;

import org.example.backendkickunity.chat.domain.ChatMessage;
import org.example.backendkickunity.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방에 속한 메시지들 조회 (최신 메시지 먼저)
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom = :chatRoom ORDER BY cm.createdDate DESC")
    List<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
}
