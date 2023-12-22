package com.example.web.services;

import com.example.web.dto.SubjectsDto;
import com.example.web.models.FollowToSubject;
import com.example.web.models.RateOfSubjects;
import com.example.web.models.Subjects;

import java.util.List;

public interface SubjectService {
    List<SubjectsDto> findAllSubjects();
    Subjects saveSubject(SubjectsDto subjectsDto);
    SubjectsDto findSubjectById(Long clubId);
    void updateSubject(SubjectsDto club);
    void delete(Long clubId);
    List<SubjectsDto> searchSubjects(String query);
    FollowToSubject followToSubject(Long subjectId);

    boolean followed(Long userId, Long subjectId);

//    void unfollowToSubject(Long subjectId);

    List<FollowToSubject> allSubjectsUserFollowed(Long userId);

    long countOfSubject();

    List<String> getAllTypeOfSubjects();

    List<Integer> getCountOfAllSubjects();

    List<Object[]> countFollowedPeopleByDate();

    Long countOfRated(Long subjectId);

    Long sumOfRatesBySubjectId(Long subjectId);

    Integer averageRate(Long subjectId);

    Float floatAverageRate(Long subjectId);

    RateOfSubjects rateTheSubject(RateOfSubjects rate);
}
