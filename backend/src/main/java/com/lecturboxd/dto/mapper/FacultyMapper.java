package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.repository.SemesterRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.stereotype.Component;

/**
 * EN: Maps Faculty entities to FacultyResponse DTOs, including related semester counts.
 * KA: Faculty ერთეულებს FacultyResponse DTO-ებად გარდაქმნის, დაკავშირებული სემესტრების რაოდენობის ჩათვლით.
 */
@Component
public class FacultyMapper {

    private final SemesterRepository semesterRepository;

    public FacultyMapper(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    /**
     * EN: Builds a FacultyResponse with id, name, semester count, and timestamps.
     * KA: ქმნის FacultyResponse-ს id-ით, სახელით, სემესტრების რაოდენობით და დროის ნიშნულებით.
     */
    public FacultyResponse toResponse(Faculty faculty) {
        long semesterCount = semesterRepository.countByFacultyId(faculty.getId());
        return new FacultyResponse(
                faculty.getId(),
                faculty.getName(),
                semesterCount,
                faculty.getCreatedAt(),
                faculty.getUpdatedAt()
        );
    }
}
