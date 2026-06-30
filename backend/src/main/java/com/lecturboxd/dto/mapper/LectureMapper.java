package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.LectureSessionResponse;
import com.lecturboxd.entity.Lecture;
import org.springframework.stereotype.Component;

@Component
public class LectureMapper {

    public LectureSessionResponse toSessionResponse(Lecture lecture) {
        return new LectureSessionResponse(
                lecture.getId(),
                lecture.getWeek(),
                lecture.getLectureNumber(),
                lecture.getType(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getReading()
        );
    }
}
