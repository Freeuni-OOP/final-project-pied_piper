package com.lecturboxd.service;

import com.lecturboxd.dto.request.LectureRequest;
import com.lecturboxd.dto.response.LectureResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Subject;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EN: CRUD and search operations for lecture catalog entries.
 * KA: ლექციების კატალოგის CRUD და ძებნის ოპერაციები.
 */
@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final SubjectRepository subjectRepository;

    public LectureService(LectureRepository lectureRepository, SubjectRepository subjectRepository) {
        this.lectureRepository = lectureRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * EN: Creates a new lecture under the given subject.
     * KA: ქმნის ახალ ლექციას მოცემული საგნის ქვეშ.
     */
    @Transactional
    public LectureResponse create(LectureRequest request) {
        // EN: Load parent subject from DB | KA: მშობელი საგნის ჩატვირთვა ბაზიდან
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + request.getSubjectId()));

        // EN: Persist new lecture | KA: ახალი ლექციის შენახვა
        Lecture lecture = new Lecture();
        apply(lecture, request, subject);
        return toResponse(lectureRepository.save(lecture));
    }

    /**
     * EN: Returns a lecture by id.
     * KA: აბრუნებს ლექციას ID-ით.
     */
    @Transactional(readOnly = true)
    public LectureResponse getById(Long id) {
        // EN: Load lecture from DB | KA: ლექციის ჩატვირთვა ბაზიდან
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id " + id));
        return toResponse(lecture);
    }

    /**
     * EN: Returns all lectures in the catalog.
     * KA: აბრუნებს კატალოგის ყველა ლექციას.
     */
    @Transactional(readOnly = true)
    public List<LectureResponse> getAll() {
        // EN: Load all lectures from DB | KA: ყველა ლექციის ჩატვირთვა ბაზიდან
        return lectureRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * EN: Searches lectures by title or description with pagination.
     * KA: ეძებს ლექციებს სათაურით ან აღწერით გვერდებად დაყოფით.
     */
    @Transactional(readOnly = true)
    public Page<LectureResponse> search(String query, Pageable pageable) {
        // EN: Empty query returns empty page | KA: ცარიელი მოთხოვნა აბრუნებს ცარიელ გვერდს
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            return Page.empty(pageable);
        }
        // EN: DB search by title or description | KA: ბაზაში ძებნა სათაურით ან აღწერით
        return lectureRepository.searchByTitleOrDescription(trimmed, pageable).map(this::toResponse);
    }

    /**
     * EN: Updates an existing lecture and its subject association.
     * KA: განაახლებს არსებულ ლექციას და მის საგანთან კავშირს.
     */
    @Transactional
    public LectureResponse update(Long id, LectureRequest request) {
        // EN: Load lecture and target subject from DB | KA: ლექციისა და სამიზნე საგნის ჩატვირთვა ბაზიდან
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id " + id));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + request.getSubjectId()));

        // EN: Apply fields and save | KA: ველების გამოყენება და შენახვა
        apply(lecture, request, subject);
        return toResponse(lectureRepository.save(lecture));
    }

    /**
     * EN: Deletes a lecture by id if it exists.
     * KA: შლის ლექციას ID-ით, თუ ის არსებობს.
     */
    @Transactional
    public void delete(Long id) {
        // EN: Ensure lecture exists before delete | KA: ლექციის არსებობის შემოწმება წაშლამდე
        if (!lectureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lecture not found with id " + id);
        }
        // EN: Delete lecture from DB | KA: ლექციის წაშლა ბაზიდან
        lectureRepository.deleteById(id);
    }

    private void apply(Lecture lecture, LectureRequest request, Subject subject) {
        lecture.setSubject(subject);
        lecture.setWeek(request.getWeek());
        lecture.setLectureNumber(request.getLectureNumber());
        lecture.setType(request.getType());
        lecture.setTitle(request.getTitle().trim());
        lecture.setDescription(request.getDescription());
        lecture.setReading(request.getReading());
    }

    private LectureResponse toResponse(Lecture lecture) {
        return new LectureResponse(
                lecture.getId(),
                lecture.getSubject().getId(),
                lecture.getWeek(),
                lecture.getLectureNumber(),
                lecture.getType(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getReading()
        );
    }
}
