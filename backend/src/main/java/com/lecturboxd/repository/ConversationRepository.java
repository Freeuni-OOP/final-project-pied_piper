package com.lecturboxd.repository;

import com.lecturboxd.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Find a conversation between two users (order-independent)
     */
    @Query("SELECT c FROM Conversation c WHERE " +
           "((c.user1.id = :userId1 AND c.user2.id = :userId2) OR " +
           "(c.user1.id = :userId2 AND c.user2.id = :userId1))")
    Optional<Conversation> findBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    /**
     * Find all conversations for a specific user, ordered by most recent update
     */
    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId " +
           "ORDER BY c.updatedAt DESC")
    List<Conversation> findAllForUser(@Param("userId") UUID userId);

    /**
     * Find conversations by user with a specific other user
     */
    List<Conversation> findByUser1IdOrUser2Id(UUID user1Id, UUID user2Id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    void deleteAllForUser(@Param("userId") UUID userId);
}
