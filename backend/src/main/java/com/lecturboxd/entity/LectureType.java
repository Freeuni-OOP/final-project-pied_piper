package com.lecturboxd.entity;

/**
 * EN: Enum of syllabus session kinds stored on the lectures table.
 * KA: სილაბუსის სესიის ტიპების ენუმი, რომელიც ინახება lectures ცხრილში.
 */
public enum LectureType {
    // EN: Standard lecture session | KA: სტანდარტული ლექციის სესია
    LECTURE,
    // EN: Seminar / discussion session | KA: სემინარი / დისკუსიის სესია
    SEMINAR,
    // EN: Laboratory / practical session | KA: ლაბორატორია / პრაქტიკული სესია
    LAB,
    // EN: Exam event | KA: გამოცდის ღონისძიება
    EXAM,
    // EN: Assignment or coursework deadline | KA: დავალების ან კურსის ვადა
    DEADLINE,
    // EN: Student or course presentation | KA: სტუდენტის ან კურსის პრეზენტაცია
    PRESENTATION
}
