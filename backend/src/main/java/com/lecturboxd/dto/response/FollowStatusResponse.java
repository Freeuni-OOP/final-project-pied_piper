package com.lecturboxd.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class FollowStatusResponse {

    private UUID userId;

    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public FollowStatusResponse() {
    }

    public FollowStatusResponse(UUID userId, boolean isFollowing) {
        this.userId = userId;
        this.isFollowing = isFollowing;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @JsonProperty("isFollowing")
    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
