package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.LectureSessionResponse;
import com.lecturboxd.entity.Lecture;
import org.springframework.stereotype.Component;

/**
 * EN: Maps Lecture entities to session-oriented response DTOs used in syllabi.
 * KA: Lecture ერთეულებს სილაბუსში გამოყენებულ სესიის პასუხის DTO-ებად გარდაქმნის.
 */
@Component
public class LectureMapper {

    /**
     * EN: Converts a Lecture into a LectureSessionResponse (week, number, type, title, description, reading).
     * KA: Lecture-ს LectureSessionResponse-ად გარდაქმნის (კვირა, ნომერი, ტიპი, სათაური, აღწერა, საკითხავი).
     */
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
