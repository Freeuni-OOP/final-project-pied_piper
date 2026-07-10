package com.lecturboxd.repository;

import com.lecturboxd.entity.Follow;
import com.lecturboxd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * EN: Spring Data repository for Follow social-graph edges.
 * KA: Spring Data რეპოზიტორი Follow სოციალური გრაფის კავშირებისთვის.
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * EN: Whether follower already follows followed (by UUID ids).
     * KA: მიჰყვება თუ არა უკვე follower followed-ს (UUID id-ებით).
     */
    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    /**
     * EN: Followers of a user, newest follow first.
     * KA: მომხმარებლის გამომწერები, უახლესი გამოწერა პირველი.
     */
    List<Follow> findByFollowedIdOrderByCreatedAtDesc(UUID followedId);

    /**
     * EN: Users that a given user follows, newest follow first.
     * KA: მომხმარებლები, რომლებსაც მოცემული მომხმარებელი მიჰყვება, უახლესი გამოწერა პირველი.
     */
    List<Follow> findByFollowerIdOrderByCreatedAtDesc(UUID followerId);

    /**
     * EN: Follower count for a profile (how many follow this user).
     * KA: პროფილის გამომწერების რაოდენობა (რამდენი მიჰყვება ამ მომხმარებელს).
     */
    long countByFollowedId(UUID followedId);

    /**
     * EN: Following count for a profile (how many this user follows).
     * KA: პროფილის გამოწერების რაოდენობა (რამდენს მიჰყვება ეს მომხმარებელი).
     */
    long countByFollowerId(UUID followerId);

    /**
     * EN: Removes the follow edge between two users by id.
     * KA: შლის გამოწერის კავშირს ორ მომხმარებელს შორის id-ებით.
     */
    void deleteByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    /**
     * EN: Whether a follow edge exists between two User entities.
     * KA: არსებობს თუ არა გამოწერის კავშირი ორ User ენთითს შორის.
     */
    boolean existsByFollowerAndFollowed(User follower, User followed);

    /**
     * EN: Deletes all follow edges where the user is follower or followed (account cleanup).
     * KA: შლის ყველა გამოწერის კავშირს, სადაც მომხმარებელი არის follower ან followed (ანგარიშის გასუფთავება).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Follow f WHERE f.follower.id = :userId OR f.followed.id = :userId")
    void deleteAllForUser(@Param("userId") UUID userId);
}
