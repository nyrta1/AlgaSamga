package com.example.web.services.impl;

import com.example.web.dto.SubjectsDto;
import com.example.web.models.*;
import com.example.web.repository.FollowToSubjectRepository;
import com.example.web.repository.RateOfSubjectsRepository;
import com.example.web.repository.SubjectsRepository;
import com.example.web.repository.UserRepository;
import com.example.web.security.SecurityUtil;
import com.example.web.services.SubjectService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.example.web.mapper.SubjectMapper.mapToSubject;
import static com.example.web.mapper.SubjectMapper.mapToSubjectDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {
    private SubjectsRepository subjectsRepository;
    private UserRepository userRepository;
    private FollowToSubjectRepository followToSubjectRepository;
    private RateOfSubjectsRepository rateOfSubjectsRepository;

    @Autowired
    public SubjectServiceImpl(SubjectsRepository subjectsRepository, UserRepository userRepository, FollowToSubjectRepository followToSubjectRepository, RateOfSubjectsRepository rateOfSubjectsRepository) {
        this.subjectsRepository = subjectsRepository;
        this.userRepository = userRepository;
        this.followToSubjectRepository = followToSubjectRepository;
        this.rateOfSubjectsRepository = rateOfSubjectsRepository;
    }

    @Override
    public Subjects saveSubject(SubjectsDto subjectsDto) {
        String username = SecurityUtil.getSessionUser();
        UserEntity user = userRepository.findByUsername(username);
        Subjects subject = mapToSubject(subjectsDto);
        subject.setCreatedBy(user);
        return subjectsRepository.save(subject);
    }

    @Override
    public List<SubjectsDto> findAllSubjects() {
        List<Subjects> subjects = subjectsRepository.findAll(Sort.by(Sort.Direction.DESC, "rate"));
        return subjects.stream().map(subject -> mapToSubjectDto(subject)).collect(Collectors.toList());
    }

    @Override
    public SubjectsDto findSubjectById(Long subjectId) {
        Subjects subjects =subjectsRepository.findById(subjectId).get();
        return mapToSubjectDto(subjects);
    }

    @Override
    public void updateSubject(SubjectsDto clubDto) {
        String username = SecurityUtil.getSessionUser();
        UserEntity user = userRepository.findByUsername(username);
        Subjects subjects = mapToSubject(clubDto);
        subjects.setCreatedBy(user);
        subjectsRepository.save(subjects);
    }

    @Override
    public void delete(Long clubId) {
        subjectsRepository.deleteById(clubId);
    }

    @Override
    public List<SubjectsDto> searchSubjects(String query) {
        List<Subjects> subjects = subjectsRepository.searchSubjects(query);
        return subjects.stream().map(subjects1 -> mapToSubjectDto(subjects1)).collect(Collectors.toList());
    }

    @Override
    public FollowToSubject followToSubject(Long subjectId) {
        FollowToSubject follow = new FollowToSubject();

        follow.setSubjects(
                subjectsRepository.findById(subjectId).get()
        );

        follow.setUser(
                userRepository.findByUsername(
                        SecurityUtil.getSessionUser()
                )
        );

        return followToSubjectRepository.save(follow);
    }

    @Override
    public boolean followed(Long userId, Long subjectId) {
        List<FollowToSubject> followToSubjectList = followToSubjectRepository.findFollowed(userId, subjectId);
        return !followToSubjectList.isEmpty();
    }

//    @Override
//    public void unfollowToSubject(Long subjectId) {
//        followToSubjectRepository.unfollow(userRepository.findByUsername(SecurityUtil.getSessionUser()).getId(), subjectId);
//    }

    @Override
    public List<FollowToSubject> allSubjectsUserFollowed(Long userId) {
        List<FollowToSubject> followToSubjectList = followToSubjectRepository.findAllFollowed(userId);
        return followToSubjectList;
    }

    @Override
    public long countOfSubject() {
        return subjectsRepository.count();
    }

    @Override
    public List<String> getAllTypeOfSubjects() {
        List<String> allType = subjectsRepository.allTypeOfSubjects();
        return allType;
    }

    @Override
    public List<Integer> getCountOfAllSubjects() {
        List<Integer> allLessonCount = new ArrayList<>();

        List<Subjects> allSubjects = subjectsRepository.findAll(); // Fetch all subjects

        for (Subjects subject : allSubjects) {
            List<Lesson> lessons = subject.getLessons(); // Get the lessons for each subject
            allLessonCount.add(lessons.size());
        }

        return allLessonCount;
    }

    @Override
    public List<Object[]> countFollowedPeopleByDate() {
        return followToSubjectRepository.countFollowedPeopleByDate();
    }

    @Override
    public Long countOfRated(Long subjectId) {
        return rateOfSubjectsRepository.countOfRated(subjectId);
    }

    @Override
    public Long sumOfRatesBySubjectId(Long subjectId) {
        if (rateOfSubjectsRepository.sumOfRatesBySubjectId(subjectId) == null) {
            return Long.valueOf(0);
        }
        return rateOfSubjectsRepository.sumOfRatesBySubjectId(subjectId);
    }

    @Override
    public Integer averageRate(Long subjectId) {
        Long countOfRated = rateOfSubjectsRepository.countOfRated(subjectId);
        Long sumOfRates = rateOfSubjectsRepository.sumOfRatesBySubjectId(subjectId);

        if (sumOfRates == null || sumOfRates == 0) {
            return 0;
        }

        return (int) (sumOfRates / countOfRated);
    }

    @Override
    public Float floatAverageRate(Long subjectId) {
        Long countOfRated = rateOfSubjectsRepository.countOfRated(subjectId);
        Long sumOfRates = rateOfSubjectsRepository.sumOfRatesBySubjectId(subjectId);

        if (sumOfRates == null || sumOfRates == 0 || countOfRated == null || countOfRated == 0) {
            return 0f;
        }

        float averageRate = (float) sumOfRates / countOfRated;
        return averageRate;
    }

    @Override
    @Transactional
    public RateOfSubjects rateTheSubject(RateOfSubjects rate) {
        Long userId = rate.getUserId().getId();
        Long subjectId = rate.getSubjectId().getId();

        if (!rateOfSubjectsRepository.checkUserIdExists(userId, subjectId)) {
            rateOfSubjectsRepository.save(rate);
        } else {
            rateOfSubjectsRepository.updateTheRate(rate.getRate(), userId, subjectId);
            rateOfSubjectsRepository.flush();
        }
        subjectsRepository.updateTheRate(subjectId, floatAverageRate(subjectId));
        return rate;
    }
}
