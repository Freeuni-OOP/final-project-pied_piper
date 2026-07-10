package com.lecturboxd.dto.response;

import java.util.UUID;

/**
 * EN: Public/profile view of a user including social and activity counters.
 * KA: მომხმარებლის საჯარო/პროფილის ხედი სოციალური და აქტივობის მთვლელებით.
 */
public class UserProfileResponse {

    private UUID id;
    private String name;
    private String email;
    private long followerCount;
    private long followingCount;
    private long reviewCount;
    private long lectureLogCount;

    public UserProfileResponse() {
    }

    public UserProfileResponse(
            UUID id,
            String name,
            String email,
            long followerCount,
            long followingCount,
            long reviewCount,
            long lectureLogCount
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.reviewCount = reviewCount;
        this.lectureLogCount = lectureLogCount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public long getLectureLogCount() {
        return lectureLogCount;
    }

    public void setLectureLogCount(long lectureLogCount) {
        this.lectureLogCount = lectureLogCount;
    }
}
