package com.lecturboxd.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * EN: Full subject syllabus response with sessions grouped by type (lecture, seminar, lab, etc.).
 * KA: საგნის სრული სილაბუსის პასუხი სესიებით დაჯგუფებული ტიპის მიხედვით (ლექცია, სემინარი, ლაბი და სხვ.).
 */
public class SubjectSyllabusResponse {

    private Long id;
    private String name;
    private String lecturer;
    private String type;
    private String description;
    private Long semesterId;
    /** EN: Sessions of type lecture. KA: ლექციის ტიპის სესიები. */
    private List<LectureSessionResponse> lectures = new ArrayList<>();
    /** EN: Sessions of type seminar. KA: სემინარის ტიპის სესიები. */
    private List<LectureSessionResponse> seminars = new ArrayList<>();
    /** EN: Sessions of type lab. KA: ლაბორატორიის ტიპის სესიები. */
    private List<LectureSessionResponse> labs = new ArrayList<>();
    /** EN: Sessions of type exam. KA: გამოცდის ტიპის სესიები. */
    private List<LectureSessionResponse> exams = new ArrayList<>();
    /** EN: Sessions of type deadline. KA: ვადის (deadline) ტიპის სესიები. */
    private List<LectureSessionResponse> deadlines = new ArrayList<>();
    /** EN: Sessions of type presentation. KA: პრეზენტაციის ტიპის სესიები. */
    private List<LectureSessionResponse> presentations = new ArrayList<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public List<LectureSessionResponse> getLectures() {
        return lectures;
    }

    public void setLectures(List<LectureSessionResponse> lectures) {
        this.lectures = lectures == null ? new ArrayList<>() : lectures;
    }

    public List<LectureSessionResponse> getSeminars() {
        return seminars;
    }

    public void setSeminars(List<LectureSessionResponse> seminars) {
        this.seminars = seminars == null ? new ArrayList<>() : seminars;
    }

    public List<LectureSessionResponse> getLabs() {
        return labs;
    }

    public void setLabs(List<LectureSessionResponse> labs) {
        this.labs = labs == null ? new ArrayList<>() : labs;
    }

    public List<LectureSessionResponse> getExams() {
        return exams;
    }

    public void setExams(List<LectureSessionResponse> exams) {
        this.exams = exams == null ? new ArrayList<>() : exams;
    }

    public List<LectureSessionResponse> getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(List<LectureSessionResponse> deadlines) {
        this.deadlines = deadlines == null ? new ArrayList<>() : deadlines;
    }

    public List<LectureSessionResponse> getPresentations() {
        return presentations;
    }

    public void setPresentations(List<LectureSessionResponse> presentations) {
        this.presentations = presentations == null ? new ArrayList<>() : presentations;
    }
}
