package com.lecturboxd.repository;

import com.lecturboxd.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Get paginated messages for a conversation ordered by sent time descending (newest first)
     */
    Page<ChatMessage> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);

    /**
     * Get all unread messages for a receiver
     */
    List<ChatMessage> findByReceiverIdAndReadFalse(UUID receiverId);

    /**
     * Get unread count for a specific receiver
     */
    long countByReceiverIdAndReadFalse(UUID receiverId);

    /**
     * Mark all messages in a conversation as read for a specific receiver
     */
    @Query("UPDATE ChatMessage m SET m.read = true WHERE m.conversation.id = :conversationId AND m.receiver.id = :receiverId AND m.read = false")
    void markConversationAsRead(@Param("conversationId") Long conversationId, @Param("receiverId") UUID receiverId);

    /**
     * Mark a specific message as read
     */
    Optional<ChatMessage> findById(Long messageId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ChatMessage m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    void deleteAllForUser(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ChatMessage m WHERE m.conversation.id IN :conversationIds")
    void deleteByConversationIdIn(@Param("conversationIds") List<Long> conversationIds);
}
