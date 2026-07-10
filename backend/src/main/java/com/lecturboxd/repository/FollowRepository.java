package com.lecturboxd.repository;

import com.lecturboxd.entity.Follow;
import com.lecturboxd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    List<Follow> findByFollowedIdOrderByCreatedAtDesc(UUID followedId);

    List<Follow> findByFollowerIdOrderByCreatedAtDesc(UUID followerId);

    long countByFollowedId(UUID followedId);

    long countByFollowerId(UUID followerId);

    void deleteByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    boolean existsByFollowerAndFollowed(User follower, User followed);
}
