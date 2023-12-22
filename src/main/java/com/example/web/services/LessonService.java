package com.example.web.services;

import com.example.web.dto.LessonDto;

import java.util.List;

public interface LessonService {
    void createLesson(Long subjectId, LessonDto lessonDto);
    List<LessonDto> findAllLessons();
    LessonDto findByLessonId(Long lessonId);
    void updateLesson(LessonDto lessonDto);
    void deleteLesson(Long lessonId);
    List<LessonDto> searchLesson(String query);
    void saveLesson(Long subjectId, LessonDto lessonId);

    void markAsWatched(Long subjectId, Long lessonId, Long userId);

    void unmarkAsWatched(Long subjectId, Long lessonId, Long userId);

    boolean knowTheLessonMarkedOrNot(Long subjectId, Long lessonId, Long userId);

    void userSubjectProgress(Long subjectId, Long lessonId, Long userId);
}
