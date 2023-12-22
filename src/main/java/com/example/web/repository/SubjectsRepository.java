package com.example.web.repository;

import com.example.web.models.Lesson;
import com.example.web.models.Subjects;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectsRepository extends JpaRepository<Subjects, Long> {
    @Query("SELECT s FROM Subjects s WHERE s.title LIKE CONCAT('%', :query, '%')")
    List<Subjects> searchSubjects(String query);

    @Transactional
    @Modifying
    @Query("UPDATE Subjects s SET s.allLongitude = s.allLongitude + :longitudeToAdd WHERE s.id = :subjectId")
    void addLongitude(@Param("subjectId") Long subjectId, @Param("longitudeToAdd") int longitudeToAdd);

    long count();

    @Query("SELECT s.type FROM Subjects s")
    List<String> allTypeOfSubjects();

    @Modifying
    @Query("UPDATE Subjects s SET s.rate = :rate WHERE  s.id = :subjectId")
    void updateTheRate(@Param("subjectId") Long subjectId, @Param("rate") Float rate);

}
