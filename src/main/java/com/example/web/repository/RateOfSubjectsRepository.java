package com.example.web.repository;

import com.example.web.models.RateOfSubjects;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RateOfSubjectsRepository extends JpaRepository<RateOfSubjects, Long> {
    @Query("SELECT COUNT(r) FROM RateOfSubjects r WHERE r.subjectId.id = :subjectId")
    Long countOfRated(Long subjectId);

    @Query("SELECT SUM(r.rate) FROM RateOfSubjects r WHERE r.subjectId.id = :subjectId")
    Long sumOfRatesBySubjectId(Long subjectId);

    @Query("SELECT COUNT(r) > 0 FROM RateOfSubjects r WHERE r.subjectId.id = :subjectId AND r.userId.id = :userId")
    boolean checkUserIdExists(Long userId,Long subjectId);

    @Transactional
    @Modifying
    @Query("UPDATE RateOfSubjects r SET r.rate = :rate WHERE r.userId.id = :userId AND r.subjectId.id = :subjectId")
    void updateTheRate(Integer rate, Long userId, Long subjectId);
}
