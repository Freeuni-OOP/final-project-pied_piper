package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.repository.SemesterRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.stereotype.Component;

@Component
public class FacultyMapper {

    private final SemesterRepository semesterRepository;

    public FacultyMapper(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

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
