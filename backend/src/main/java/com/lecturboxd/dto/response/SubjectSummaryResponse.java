package com.lecturboxd.dto.response;

public class SubjectSummaryResponse {

    private Long id;
    private String name;
    private String lecturer;
    private String type;

    public SubjectSummaryResponse() {
    }

    public SubjectSummaryResponse(Long id, String name, String lecturer, String type) {
        this.id = id;
        this.name = name;
        this.lecturer = lecturer;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
