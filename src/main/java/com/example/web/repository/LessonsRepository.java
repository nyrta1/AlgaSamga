package com.example.web.repository;

import com.example.web.dto.LessonDto;
import com.example.web.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonsRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l FROM Lesson l WHERE l.description LIKE CONCAT('%', :query, '%')")
    List<Lesson> searchLesson(String query);

    @Query("SELECT COUNT(*) FROM Lesson l WHERE l.subjects.id = :subjectId")
    Long countOfLessonByID(Long subjectId);
}
