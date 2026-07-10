package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.LectureSessionResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LectureMapperTest {

    private final LectureMapper lectureMapper = new LectureMapper();

    @Test
    void toSessionResponseMapsFields() {
        Lecture lecture = new Lecture();
        lecture.setId(3L);
        lecture.setWeek(2);
        lecture.setLectureNumber(1);
        lecture.setType(LectureType.LAB);
        lecture.setTitle("Lab");
        lecture.setDescription("desc");
        lecture.setReading("ch1");

        LectureSessionResponse response = lectureMapper.toSessionResponse(lecture);

        assertEquals(3L, response.getId());
        assertEquals(2, response.getWeek());
        assertEquals(1, response.getLectureNumber());
        assertEquals(LectureType.LAB, response.getType());
        assertEquals("Lab", response.getTitle());
        assertEquals("desc", response.getDescription());
        assertEquals("ch1", response.getReading());
    }
}
