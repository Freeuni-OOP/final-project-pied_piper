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

/**
 * EN: Spring Data repository for ChatMessage entities.
 * KA: Spring Data რეპოზიტორი ChatMessage ენთითებისთვის.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * EN: Paginated messages for a conversation ordered by sent time descending (newest first).
     * KA: საუბრის შეტყობინებები გვერდებად, გაგზავნის დროის კლებადობით (უახლესი პირველი).
     */
    Page<ChatMessage> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);

    /**
     * EN: All unread messages for a given receiver across conversations.
     * KA: მოცემული მიმღების ყველა წაუკითხავი შეტყობინება ყველა საუბრიდან.
     */
    List<ChatMessage> findByReceiverIdAndReadFalse(UUID receiverId);

    /**
     * EN: Count of unread messages for a receiver inside one conversation.
     * KA: წაუკითხავი შეტყობინებების რაოდენობა მიმღებისთვის ერთ საუბარში.
     */
    long countByConversationIdAndReceiverIdAndReadFalse(Long conversationId, UUID receiverId);

    /**
     * EN: Marks all unread messages in a conversation as read for the given receiver.
     * KA: საუბარში ყველა წაუკითხავ შეტყობინებას წაკითხულად ნიშნავს მოცემული მიმღებისთვის.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatMessage m SET m.read = true WHERE m.conversation.id = :conversationId AND m.receiver.id = :receiverId AND m.read = false")
    int markConversationAsRead(@Param("conversationId") Long conversationId, @Param("receiverId") UUID receiverId);

    /**
     * EN: Loads a single message by primary key (used when marking one message as read).
     * KA: იტვირთავს ერთ შეტყობინებას პირველადი გასაღებით (ერთი შეტყობინების წაკითხულად მონიშვნისთვის).
     */
    Optional<ChatMessage> findById(Long messageId);

    /**
     * EN: Deletes every message where the user is sender or receiver (account cleanup).
     * KA: შლის ყველა შეტყობინებას, სადაც მომხმარებელი გამგზავნი ან მიმღებია (ანგარიშის გასუფთავება).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ChatMessage m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    void deleteAllForUser(@Param("userId") UUID userId);

    /**
     * EN: Deletes all messages belonging to any of the given conversation IDs.
     * KA: შლის ყველა შეტყობინებას მოცემული საუბრის ID-ებისთვის.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ChatMessage m WHERE m.conversation.id IN :conversationIds")
    void deleteByConversationIdIn(@Param("conversationIds") List<Long> conversationIds);
}
