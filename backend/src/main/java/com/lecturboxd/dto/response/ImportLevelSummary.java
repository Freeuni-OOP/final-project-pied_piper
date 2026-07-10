package com.lecturboxd.dto.response;

/**
 * EN: Counters for how many entities were created vs skipped at one import hierarchy level.
 * KA: მთვლელები იმისა, რამდენი ერთეული შეიქმნა და რამდენი გამოტოვდა იმპორტის ერთ იერარქიულ დონეზე.
 */
public class ImportLevelSummary {

    /** EN: Entities newly inserted at this level. KA: ამ დონეზე ახლად ჩასმული ერთეულები. */
    private int created;
    /** EN: Entities skipped because they already existed (or were invalid). KA: გამოტოვებული ერთეულები, რადგან უკვე არსებობდნენ (ან არასწორი იყვნენ). */
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
