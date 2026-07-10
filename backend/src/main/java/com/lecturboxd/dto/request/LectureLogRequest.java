package com.lecturboxd.dto.request;

import java.time.LocalDate;

/**
 * EN: Request body for logging that a user watched/attended a lecture.
 * KA: მოთხოვნის სხეული იმის აღსანიშნავად, რომ მომხმარებელმა ლექცია ნახა/დაესწრო.
 */
public class LectureLogRequest {

    /** EN: Optional date the lecture was watched; may default server-side if omitted. KA: ლექციის ნახვის სურვილისამებრი თარიღი; გამოტოვებისას სერვერმა შეიძლება ნაგულისხმევი დააყენოს. */
    private LocalDate watchedAt;

    public LocalDate getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(LocalDate watchedAt) {
        this.watchedAt = watchedAt;
    }
}
