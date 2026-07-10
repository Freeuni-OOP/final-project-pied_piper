package com.lecturboxd.dto.response;

import com.lecturboxd.entity.LectureType;

/**
 * EN: Compact lecture/session DTO used inside syllabus groupings (without subject id).
 * KA: კომპაქტური ლექციის/სესიის DTO სილაბუსის დაჯგუფებებში გამოსაყენებლად (საგნის id-ის გარეშე).
 */
public class LectureSessionResponse {

    private Long id;
    private Integer week;
    private Integer lectureNumber;
    private LectureType type;
    private String title;
    private String description;
    private String reading;

    public LectureSessionResponse() {
    }

    public LectureSessionResponse(
            Long id,
            Integer week,
            Integer lectureNumber,
            LectureType type,
            String title,
            String description,
            String reading
    ) {
        this.id = id;
        this.week = week;
        this.lectureNumber = lectureNumber;
        this.type = type;
        this.title = title;
        this.description = description;
        this.reading = reading;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getLectureNumber() {
        return lectureNumber;
    }

    public void setLectureNumber(Integer lectureNumber) {
        this.lectureNumber = lectureNumber;
    }

    public LectureType getType() {
        return type;
    }

    public void setType(LectureType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }
}
