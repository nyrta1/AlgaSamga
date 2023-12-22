package com.example.web.repository;

import com.example.web.models.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findCommentById(Long id);
    @Query("SELECT c FROM Comment c WHERE c.lesson.id = :lessonId")
    List<Comment> findAllCommentsForLesson(Long lessonId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.lesson.id = :lessonId")
    void deleteCommentsByLessonId(Long lessonId);
}
