package com.lecturboxd.entity;

/**
 * EN: Enum of feed activity kinds stored on the activities table.
 * KA: ფიდის აქტივობის ტიპების ენუმი, რომელიც ინახება activities ცხრილში.
 */
public enum ActivityType {
    // EN: User published a lecture review | KA: მომხმარებელმა გამოაქვეყნა ლექციის მიმოხილვა
    REVIEW_CREATED,
    // EN: User marked a lecture as watched/logged | KA: მომხმარებელმა ლექცია მონიშნა ნანახად/დაალოგა
    LECTURE_LOGGED
}
