package com.lecturboxd.dto.response;

public class ImportSummaryResponse {

    private ImportLevelSummary faculties = new ImportLevelSummary();
    private ImportLevelSummary semesters = new ImportLevelSummary();
    private ImportLevelSummary subjects = new ImportLevelSummary();
    private ImportLevelSummary lectures = new ImportLevelSummary();

    public ImportLevelSummary getFaculties() {
        return faculties;
    }

    public void setFaculties(ImportLevelSummary faculties) {
        this.faculties = faculties;
    }

    public ImportLevelSummary getSemesters() {
        return semesters;
    }

    public void setSemesters(ImportLevelSummary semesters) {
        this.semesters = semesters;
    }

    public ImportLevelSummary getSubjects() {
        return subjects;
    }

    public void setSubjects(ImportLevelSummary subjects) {
        this.subjects = subjects;
    }

    public ImportLevelSummary getLectures() {
        return lectures;
    }

    public void setLectures(ImportLevelSummary lectures) {
        this.lectures = lectures;
    }
}
