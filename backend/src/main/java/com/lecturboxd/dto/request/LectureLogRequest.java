package com.lecturboxd.dto.request;

import java.time.LocalDate;

public class LectureLogRequest {

    private LocalDate watchedAt;

    public LocalDate getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(LocalDate watchedAt) {
        this.watchedAt = watchedAt;
    }
}
