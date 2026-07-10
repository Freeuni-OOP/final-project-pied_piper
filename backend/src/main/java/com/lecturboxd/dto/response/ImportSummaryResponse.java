package com.lecturboxd.dto.response;

/**
 * EN: Aggregate import result with per-level created/skipped summaries for the faculty tree.
 * KA: იმპორტის აგრეგირებული შედეგი დონეების მიხედვით შექმნილი/გამოტოვებული შეჯამებებით ფაკულტეტის ხისთვის.
 */
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
