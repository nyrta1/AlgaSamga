package com.example.web.repository;

import com.example.web.models.UsersLessonProgress;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgressRepository extends JpaRepository<UsersLessonProgress, Long> {
    @Query("SELECT CASE WHEN COUNT(ulp) > 0 THEN true ELSE false END " +
            "FROM UsersLessonProgress ulp " +
            "WHERE ulp.subject.id = :subjectId " +
            "AND ulp.lesson.id = :lessonId " +
            "AND ulp.user.id = :userId")
    boolean knowTheLessonMarkedOrNotSQL(
            @Param("subjectId") Long subjectId,
            @Param("lessonId") Long lessonId,
            @Param("userId") Long userId
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM UsersLessonProgress ulp " +
            "WHERE ulp.subject.id = :subjectId " +
            "AND ulp.lesson.id = :lessonId " +
            "AND ulp.user.id = :userId")
    void deleteTheProgressSQL(
            @Param("subjectId") Long subjectId,
            @Param("lessonId") Long lessonId,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(ulp) " +
            "FROM UsersLessonProgress ulp " +
            "WHERE ulp.subject.id = :subjectId " +
            "AND ulp.user.id = :userId")
    Long countOfWatchedLessons(
            @Param("subjectId") Long subjectId,
            @Param("userId") Long userId
    );
}
