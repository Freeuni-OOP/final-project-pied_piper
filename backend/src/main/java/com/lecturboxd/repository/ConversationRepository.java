package com.lecturboxd.repository;

import com.lecturboxd.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * EN: Spring Data repository for Conversation entities.
 * KA: Spring Data რეპოზიტორი Conversation ენთითებისთვის.
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * EN: Finds the conversation between two users regardless of user1/user2 order.
     * KA: პოულობს საუბარს ორ მომხმარებელს შორის user1/user2 რიგის მიუხედავად.
     */
    @Query("SELECT c FROM Conversation c WHERE " +
           "((c.user1.id = :userId1 AND c.user2.id = :userId2) OR " +
           "(c.user1.id = :userId2 AND c.user2.id = :userId1))")
    Optional<Conversation> findBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    /**
     * EN: Lists all conversations for a user ordered by most recent update.
     * KA: აბრუნებს მომხმარებლის ყველა საუბარს ბოლო განახლების მიხედვით.
     */
    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId " +
           "ORDER BY c.updatedAt DESC")
    List<Conversation> findAllForUser(@Param("userId") UUID userId);

    /**
     * EN: Finds conversations where the user appears as user1 or user2 (same id passed twice typically).
     * KA: პოულობს საუბრებს, სადაც მომხმარებელი არის user1 ან user2 (ჩვეულებრივ იგივე id ორჯერ გადაეცემა).
     */
    List<Conversation> findByUser1IdOrUser2Id(UUID user1Id, UUID user2Id);

    /**
     * EN: Deletes all conversations involving the given user (account cleanup).
     * KA: შლის ყველა საუბარს, სადაც მოცემული მომხმარებელი მონაწილეობს (ანგარიშის გასუფთავება).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    void deleteAllForUser(@Param("userId") UUID userId);
}
