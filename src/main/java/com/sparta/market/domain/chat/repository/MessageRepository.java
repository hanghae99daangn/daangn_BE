package com.sparta.market.domain.chat.repository;

import com.sparta.market.domain.chat.entity.ChatMessage;
import com.sparta.market.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findAllByChatRoomOrderBySendTimeDesc(ChatRoom chatRoom, Pageable pageable);
}

