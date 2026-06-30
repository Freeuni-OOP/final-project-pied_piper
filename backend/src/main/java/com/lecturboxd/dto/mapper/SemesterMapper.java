package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.stereotype.Component;

@Component
public class SemesterMapper {

    private final SubjectRepository subjectRepository;

    public SemesterMapper(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

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
