package com.example.web.repository;

import com.example.web.models.FollowToSubject;
import com.example.web.models.Subjects;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowToSubjectRepository extends JpaRepository<FollowToSubject, Long> {
    @Transactional
    @Query("SELECT fts FROM FollowToSubject fts WHERE fts.user.id = :userId AND fts.subjects.id = :subjectId")
    List<FollowToSubject> findFollowed(Long userId, Long subjectId);

    @Transactional
    @Query("SELECT fts FROM FollowToSubject fts WHERE fts.user.id = :userId")
    List<FollowToSubject> findAllFollowed(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM FollowToSubject fts WHERE fts.user.id = :userId AND fts.subjects.id = :subjectId")
    void unfollow(Long userId, Long subjectId);

    @Query(nativeQuery = true,
            value = "SELECT DATE(t1.followed_time) AS date, " +
                    "(SELECT COUNT(*) " +
                    "FROM followed_users t2 " +
                    "WHERE DATE(t2.followed_time) <= DATE(t1.followed_time)) AS followed_people " +
                    "FROM (SELECT DISTINCT DATE(followed_time) AS followed_time FROM followed_users) t1 " +
                    "ORDER BY DATE(t1.followed_time)")
    List<Object[]> countFollowedPeopleByDate();

    @Modifying
    @Transactional
    @Query("UPDATE FollowToSubject fts " +
            "SET fts.progressPercent = :percent " +
            "WHERE fts.subjects.id = :subjectId AND fts.user.id = :userId")
    void updateTheProgressPercent(Long subjectId, Long userId, Long percent);
}
