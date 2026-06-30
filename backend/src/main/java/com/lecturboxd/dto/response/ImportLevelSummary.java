package com.lecturboxd.dto.response;

public class ImportLevelSummary {

    private int created;
    private int skipped;

    public ImportLevelSummary() {
    }

    public ImportLevelSummary(int created, int skipped) {
        this.created = created;
        this.skipped = skipped;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public void incrementCreated() {
        created++;
    }

    public void incrementSkipped() {
        skipped++;
    }
}
