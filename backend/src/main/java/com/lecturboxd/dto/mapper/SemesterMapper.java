package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.stereotype.Component;

/**
 * EN: Maps Semester entities to SemesterResponse DTOs, including subject counts.
 * KA: Semester ერთეულებს SemesterResponse DTO-ებად გარდაქმნის, საგნების რაოდენობის ჩათვლით.
 */
@Component
public class SemesterMapper {

    private final SubjectRepository subjectRepository;

    public SemesterMapper(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    /**
     * EN: Builds a SemesterResponse with id, number, faculty id, subject count, and timestamps.
     * KA: ქმნის SemesterResponse-ს id-ით, ნომრით, ფაკულტეტის id-ით, საგნების რაოდენობით და დროის ნიშნულებით.
     */
    public SemesterResponse toResponse(Semester semester) {
        long subjectCount = subjectRepository.countBySemesterId(semester.getId());
        return new SemesterResponse(
                semester.getId(),
                semester.getNumber(),
                semester.getFaculty().getId(),
                subjectCount,
                semester.getCreatedAt(),
                semester.getUpdatedAt()
        );
    }
}
